package com.imooc.controller.listenser;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.controller.job.AddressBalanceModifyJob;
import com.imooc.controller.job.DayAddressBalanceModifyJob;
import com.imooc.job.service.AddrTransferService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CollectdStarterApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
	private List<AgentSvcTask> svcs = new ArrayList<>();
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            initServices();
            startServices();
        } catch (Exception e) {
            log.warn("onapplication exception. " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void initServices() throws Exception {
        log.info("-------CollectdStarterApplicationListener-----");
    //    svcs.add(new  DayAddressBalanceModifyJob());
        svcs.add(AddressBalanceModifyJob.getInstance());
        svcs.add(AddrTransferService.getInstance());
    }

    private void startServices() {
        for (AgentSvcTask svcTask : svcs) {
            svcTask.init_svc();
        }
        for (AgentSvcTask svcTask : svcs) {
            svcTask.start();
        }
    }

}
