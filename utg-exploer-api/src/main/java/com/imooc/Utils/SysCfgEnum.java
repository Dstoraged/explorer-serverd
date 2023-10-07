package com.imooc.Utils;

import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class SysCfgEnum {

    private  String PLEDGEBLOCKKEY="sync_pledge_block";

    private  String ADDRESSBALANCEBLOCKKEY="sync_address_balance_block";

    private  String ADDRESSWITHDRAWBLOCKKEY="sync_address_withdraw_block";

    private  String ERC20BLOCKKEY="sync_erc20_block";

    private  String ERC1155BLOCKKEY="sync_erc1155_block";

    private  String TRANSACTIONBLOCKKEY="sync_transaction_block";

    private  String DPOSVOTERBLOCKKEY="sync_dpos_voter_block";

    private  String RNEXITBLOCKKEY="sync_rn_exit_block";

    private  String PUNILSHBLOCKKEY="sync_punilsh_block";

    private  String WALLETPLEDGEBLOCKKEY="sync_wallet_pledge_block";




}
