package com.shf.pyg.pay.service;

import java.util.Map;

public interface WeiXinPayService {
    /**
     * 生成微信支付二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative(String out_trade_no,String total_fee);


    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);

    /**
     * 查询支付状态(轮询)
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatusWhile(String out_trade_no);
}
