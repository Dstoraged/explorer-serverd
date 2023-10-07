package com.imooc.job.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.SpringContextUtils;
import com.imooc.Utils.TimeSpend;
import com.imooc.mapper.TransactionMapper;
import com.imooc.pojo.Transaction;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Service
public class AddrTransferService extends AgentSvcTask{

	@Data
	public class TransferPath {
		String address;
	//	List<Transaction> txs;
		Integer txcount;
		List<TransferPath> paths;
	}
	private static Object lock=new Object();
	private static AddrTransferService instance;
	//@Autowired
	TransactionMapper transactionMapper;

	private Map<String, Map<String, List<Transaction>>> transferMap = new HashMap<>();

	private long txCount = 0;
	
	protected  void runTask() {
		transactionMapper = SpringContextUtils.getBean(TransactionMapper.class);
		String transferPathEnable = SpringContextUtils.getProperty("transferPathEnable");
		if("true".equals(transferPathEnable)){
			load();
		}
	}
	
	public synchronized static AddrTransferService getInstance() {
		if(instance==null) {
			synchronized (lock) {
				if(instance==null) {
					instance=new AddrTransferService();
				}
			}
		}
		return instance;
	}
	
//	@PostConstruct
//	@Async
	private void load() {
		TimeSpend timeSpend = new TimeSpend();
		log.info("Load transferMap start.");
		transactionMapper.queryAll(new ResultHandler<Transaction>() {
			@Override
			public void handleResult(ResultContext<? extends Transaction> resultContext) {
				Transaction record = resultContext.getResultObject();
				appendTx(record);
				txCount++;
				if (txCount % 10000 == 0)
					log.info("Load " + txCount + " transaction record.");
			}
		});
		log.info("Load transferMap end, total " + transferMap.size() + " record, spend " + timeSpend.getSpendTime() + "\n");
	}
	private void appendTx(Transaction tx){
		String from = tx.getFromAddr().toLowerCase();
		String to = tx.getToAddr().toLowerCase();
		Map<String, List<Transaction>> txMap = transferMap.get(from);
		if (txMap == null) {
			txMap = new HashMap<>();
			transferMap.put(from, txMap);
		}
		List<Transaction> txList = txMap.get(to);
		if (txList == null) {
			txList = new ArrayList<>();
			txMap.put(to, txList);
		}
		txList.add(tx);
	}
	public void putTransaction(Transaction tx) {
		appendTx(tx);
	//	String address = tx.getFromAddr().toLowerCase();
	//	log.info("address "+address+" tranfer data is "+transferMap.get(address));
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param blocknumber
	 * @return
	 */
	public List<TransferPath> getTransferPath(String from, String to, Long startblock,Long endblock, Integer maxdepth) {
		if (maxdepth == null || maxdepth > 10)
			maxdepth = 10;
		Set<String> routes = new HashSet<String>();
	//	routes.add(from);
		return buildTransferPaths(from, to, startblock, endblock, 0, maxdepth, routes);
	}

	private List<TransferPath> buildTransferPaths(String from, String to, Long startblock, Long endblock, int curdepth, Integer maxdepth, Set<String> routes) {
		List<TransferPath> paths = null;
		Map<String, List<Transaction>> txMap = transferMap.get(from);
		if (txMap == null) {
			log.info("address "+from+" has no from tx");
			return paths;
		}
		curdepth++;
		log.info("address " + from + " has depth " + curdepth + " tx to " + txMap.size() + " addresses:" + txMap.keySet());
		for (String address : txMap.keySet()) {
			List<Transaction> txList = txMap.get(address);
		
		//	List<Transaction> txs = filterTransactionList(txList, blocknumber);
			List<Transaction> txs = txList.stream()
					.filter(tx -> (startblock == null || tx.getBlockNumber() >= startblock) && (endblock == null || tx.getBlockNumber() <= endblock))
					.collect(Collectors.toList());			 
			if (txs != null && txs.size() > 0) {
				if (address.equalsIgnoreCase(to)) {
					if (paths == null)
						paths = new ArrayList<>();
					TransferPath path = new TransferPath();
					path.setAddress(address);
				//	path.setTxs(txs);
					path.setTxcount(txs.size());
					paths.add(path);
					routes.add(from+"-"+address);
				} else if (address.equalsIgnoreCase(from)) {
					log.info("exclude address " + address + " self transfer");
					continue;
				} else {
//					if (routes.contains(from+"-"+address)) {
//						log.info("path " + from+"-"+address + " has already traversed");
//						continue;
//					}
					if ((maxdepth != null && curdepth >= maxdepth))
						return paths;
					routes.add(from+"-"+address);
					
					List<TransferPath> recursivePaths = buildTransferPaths(address, to, startblock,endblock, curdepth, maxdepth, routes);
					if (recursivePaths != null && recursivePaths.size() > 0) {
						if (paths == null)
							paths = new ArrayList<>();
						TransferPath path = new TransferPath();
						path.setAddress(address);
					//	path.setTxs(txs);
						path.setTxcount(txs.size());
						path.setPaths(recursivePaths);
						paths.add(path);					
					}
				}
			}
		}
		return paths;
	}

	protected List<Transaction> filterTransactionList(List<Transaction> txList, Long blocknumber) {
		List<Transaction> tranferList = null;
		for (Transaction tx : txList) {
			if (blocknumber == null || tx.getBlockNumber() >= blocknumber) {
				if (tranferList == null)
					tranferList = new ArrayList<Transaction>();
				tranferList.add(tx);
			}
		}
		return tranferList;
	}

}
