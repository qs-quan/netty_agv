package com.namei.wms.client;

import com.namei.wms.service.AgvListener;
import com.namei.wms.service.AgvService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private AgvService agvService = new AgvService();

    private AgvListener agvListener = new AgvListener();

    public static final String SPLIT = "@";

    public static final String START = asciiToString("2");

    public static final String END = asciiToString("3");

    public static final String TAST_TYPE_IN = "IN";  //入库

    public static final String TAST_TYPE_OUT = "OUT";  //出库


    //当通道就绪就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        String task = agvService.sendTask();
        String task = createTask();
        ctx.writeAndFlush(Unpooled.copiedBuffer(task, CharsetUtil.ISO_8859_1));
        System.out.println("wms发送的任务执行===" + task);
//        while (true) {
//            // todo 随时发送任务
//        }

    }

    //     task.append(START).append("接收端6").append(SPLIT).append("发送端6").append(SPLIT).append("序号2").append(SPLIT)
//                .append("确认标记1").append(SPLIT).append("报文类型2").append(SPLIT)
//                .append("ID5").append(SPLIT).append("模式6").append(SPLIT).append("应答模式6").append(SPLIT)
//                .append("AGV序号3").append(SPLIT).append("TS状态报文3").append(SPLIT).append("搬运单元Id16").append(SPLIT)
//                .append("托盘长度4").append(SPLIT).append("单元类型16").append(SPLIT).append("取料点装载水平高度4")
//                .append(SPLIT).append("送料点装卸水平高度4").append(SPLIT).append("捡取位置点5").append(SPLIT).append("送料位置点5"
//                .append(SPLIT).append("优先级1")
    private String createTask() {
        StringBuffer task = new StringBuffer();
        // STXMETRO @WMS @20@U@TO@00512@NEW @MEDIUM@000@000@000000000000@1200@STABLE@0000@0000@09901@02501@1ETX
        task.append(START).append("METRO ").append(SPLIT).append("WMS   ").append(SPLIT).append("20").append(SPLIT)
                .append("U").append(SPLIT).append("TO").append(SPLIT)
                .append("12345").append(SPLIT).append("NEW   ").append(SPLIT).append("MEDIUM").append(SPLIT)
                .append("000").append(SPLIT).append("000").append(SPLIT).append("0000000000000000").append(SPLIT)
                .append("1200").append(SPLIT).append("STABLE          ").append(SPLIT).append("0000").append(SPLIT)
                .append("0000").append(SPLIT).append("09901").append(SPLIT).append("02501").append(SPLIT).append("1")
                .append(END);
        return task.toString();
    }


    //当通道有读取事件时，会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //解析返回数据，执行对应的逻辑
        ByteBuf buf = (ByteBuf) msg;
        String message = buf.toString(CharsetUtil.ISO_8859_1);
        message = getIsoToUtf_8(message);
        System.err.println("服务器回复的消息:::::" + getIsoToUtf_8(message));
        List<String> messList = splitMessage(message);
        // todo 报文去重，防止重复消费
        messList.forEach(item -> {
            if (item.startsWith(START) && item.endsWith(END)) {
                String[] dataArr = item.split(SPLIT);
                String receiver = dataArr[0].trim();
                String ack = dataArr[3].trim();
                if (receiver.equals("WMS")) {
                    // 用户报文
                    if (ack.equals("U")) {
                        handleUserMessage(ctx, dataArr);
                    }
                    // 确认报文
                    if (ack.equals("A")) {
                        handleAckMessage(ctx, dataArr);
                    }

                }
            }
        });


    }

    private List<String> splitMessage(String message) {
        String[] split = message.split(END);

        for (int i = 0; i < split.length; i++) {
            split[i] += END;
        }
        return Arrays.asList(split);
    }

    private void handleUserMessage(ChannelHandlerContext ctx, String[] dataArr) {
        String type = dataArr[4].trim();
        switch (type) {
            //  MetRo 发送响应运输任务状态 (TS)
            case "TS": {
                handleTSMessage(ctx, dataArr);
                break;
            }
            // MetRo 发送 AGV 状态 (AS)
            case "AS": {
                handleASMessage(dataArr);
                break;
            }
            // PO = 检索报文 MetRo->WMS
            case "PO": {
                handlePOMessage(dataArr, ctx);
                break;
            }
        }
    }

    private void handleASMessage(String[] dataArr) {
        String avgId = dataArr[5].trim();
        String avgStatus = dataArr[6].trim().toUpperCase();
        System.err.println("avgId" + ":" + avgStatus);
        switch (avgStatus) {
            //REMOVED被移除
            case "REMOVED": {
                break;
            }
            // ERROR错误
            case "ERROR": {
                break;
            }
            // ESTOP急停
            case "ESTOP": {
                break;
            }
            // USERSTOP用户停止
            case "USERSTOP": {
                break;
            }
            // MANUAL手动模式
            case "MANUAL": {
                break;
            }
            // SAFETYSTOP安全停止
            case "SAFETYSTOP": {
                break;
            }
            // BLOCKED被其他AGV阻挡
            case "BLOCKED": {
                break;
            }
            // LOWBATTERY低电量
            case "LOWBATTERY": {
                break;
            }
            // PREVENTALLOCATION禁止分配任务
            case "PREVENTALLOCATION": {
                break;
            }
            // RUNNING运行中
            case "RUNNING": {
                break;
            }
        }
    }

    private void handleTSMessage(ChannelHandlerContext ctx, String[] dataArr) {
        String messageStatus = dataArr[9].trim();
        String answerMode = dataArr[7].trim();
        if (answerMode.equals("FULL")) {
            ack(ctx, dataArr);
        }
        switch (messageStatus) {
            //30 = “Accepted”“已接受”
            case "30": {
                // todo 更新任务状态
                break;
            }
            // 40 = “Allocated”“已分配”
            case "40": {
                break;
            }
            // 50 = “Fetch-Entry”“进入取料点”
            case "50": {
                // todo 更新取货点  wms->metro
                String binCode = "";
                updateTask(dataArr, ctx, TAST_TYPE_OUT, binCode);
//                                agvListener.onFetchEntry();
                break;
            }
            // 60 = “Loaded”“已装载”
            case "60": {
                // todo 1:更新任务和 减库存  2:TO‐更新送货点 wms->metro
                if (answerMode.equals("MEDIUM")) {
                    ack(ctx, dataArr);
                }
                break;
            }
            // 70 = “Delivery-Entry”“进入送料点”
            case "70": {
                // todo 更新送货位  wms->metro
                String binCode = "";
                updateTask(dataArr, ctx, TAST_TYPE_IN, binCode);
//                                agvListener.onDeliveryEntry();
                break;
            }
            // 80 = “Delivered”“已送达”
            case "80": {
                // todo 更新任务和 加库存
                if (answerMode.equals("MEDIUM")) {
                    ack(ctx, dataArr);
                }
                break;
            }
            // 90 = “NoLoadPickupPosition”“取料点无负载”
            case "90": {
                break;
            }
            // 91 = “DeliveryOccupied”“送料点被占”
            case "91": {
                break;
            }
            // 92 = “AddressError”“地址错误”
            case "92": {
                break;
            }
            // 93 = “LowerUnitMissing”“低单元缺失”
            case "93": {
                break;
            }
            // 94 = “WrongUnitType”“错误的单元类型”
            case "94": {
                break;
            }
            // 95 = “NoRead”“未读到”
            case "95": {
                break;
            }
            // 96 = “AbNormalEnd”“异常停止”
            case "96": {
                break;
            }
            // 100 = “Finished”“完成”
            case "100": {
                break;
            }
        }
    }


    private void updateTask(String[] dataArr, ChannelHandlerContext ctx, String type, String binCode) {
        if (type.equals(TAST_TYPE_IN)) {
            // 更新送货位
            dataArr[16] = binCode;
        }
        if (type.equals(TAST_TYPE_OUT)) {
            // 更新取货位
            dataArr[15] = binCode;
        }

        String task = Arrays.asList(dataArr).stream().collect(Collectors.joining(SPLIT));
        ctx.writeAndFlush(Unpooled.copiedBuffer(task, CharsetUtil.ISO_8859_1));

    }

    private void handleAckMessage(ChannelHandlerContext ctx, String[] dataArr) {

    }

    private void handlePOMessage(String[] dataArr, ChannelHandlerContext ctx) {
        dataArr[0] = START + "METRO ";
        dataArr[1] = "WMS   ";
        dataArr[3] = "A";
        dataArr[4] = "PR" + END;
        String answerMessage = Arrays.asList(dataArr).stream().collect(Collectors.joining(SPLIT));
        ctx.writeAndFlush(Unpooled.copiedBuffer(answerMessage, CharsetUtil.ISO_8859_1));
        System.err.println("wms发送消息===" + answerMessage);
    }


    private void ack(ChannelHandlerContext ctx, String[] dataArr) {
        dataArr[0] = START + "RETRO ";
        dataArr[1] = "WMS   ";
        dataArr[3] = "A";
        String answerMessage = Arrays.asList(dataArr).stream().collect(Collectors.joining(SPLIT));
        ctx.writeAndFlush(Unpooled.copiedBuffer(answerMessage, CharsetUtil.ISO_8859_1));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    public static String getIsoToUtf_8(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        String newStr = "";
        try {
            newStr = new String(str.getBytes(CharsetUtil.ISO_8859_1), CharsetUtil.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newStr;
    }

    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * Ascii转换为字符串
     *
     * @param value
     * @return
     */
    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

}
