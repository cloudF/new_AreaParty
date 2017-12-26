package com.dkzy.areaparty.phone.fragment01;

import android.util.Log;

import com.dkzy.areaparty.phone.MyConnector;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ExeInformat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.MonitorData;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ProcessFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedExeListFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedMonitorMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.RequestFormat;
import com.dkzy.areaparty.phone.utils_comman.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boris on 2016/12/14.
 */

public class prepareDataForFragment {
    private static final int DataNub = 52;
    private static double memory_used = 0;
    private static double memory_available = 0;
    private static double memory_total = 0;
    private static double net_up = 0;
    private static double net_down = 0;
    private static List<Integer> processesId = new ArrayList<>();
    private static List<ProcessFormat> currentProcesses = new ArrayList<>();
    private static List<ProcessFormat> increasedProcesses = new ArrayList<>();
    private static List<Integer> decreasedProcesses = new ArrayList<>();
    private static int[] memoryPercentArray = new int[DataNub];
    private static int[] cpuPercentArray = new int[DataNub];


    public static double getMemory_used() {
        return memory_used;
    }
    public static double getMemory_total() {
        return memory_total;
    }
    public static double getMemory_available() {
        return memory_available;
    }
    public static double getNet_up() {
        return net_up;
    }
    public static double getNet_down() {
        return net_down;
    }
    public static int[] getMemoryPercentArray() {
        return memoryPercentArray;
    }
    public static int[] getCpuPercentArray() {
        return cpuPercentArray;
    }
    public static int getMemoryPercentData() {
        return memoryPercentArray[DataNub - 1];
    }
    public static int getCpuPercentData() {
        return cpuPercentArray[DataNub - 1];
    }
    public static List<ProcessFormat> getIncreasedProcesses() {
        return increasedProcesses;
    }
    public static List<Integer> getDecreasedProcesses() {
        return decreasedProcesses;
    }


    /**
     * <summary>
     *  重置静态变量
     * </summary>
     */
    public static void initDataGroups() {
        processesId.clear();
        increasedProcesses.clear();
        decreasedProcesses.clear();
        currentProcesses.clear();
        for(int i = 0; i < 52; ++i) {
            memoryPercentArray[i] = 0;
            cpuPercentArray[i] = 0;
        }

    }

    private static void setMemoryPercentArray(int value) {
        System.arraycopy(memoryPercentArray, 1, memoryPercentArray, 0, DataNub - 1);
        memoryPercentArray[DataNub - 1] = value;
    }

    private static void setCpuPercentArray(int value) {
        System.arraycopy(cpuPercentArray, 1, cpuPercentArray, 0, DataNub - 1);
        cpuPercentArray[DataNub - 1] = value;
    }

    /**
     * 发送指令获取监控数据并更新静态变量
     */
    public static void getMonitoringData() {
        String msgReceived;
        RequestFormat request = new RequestFormat();

        request.setName(OrderConst.monitorActionData_name);
        request.setCommand(OrderConst.monitorData_get_command);
        request.setParam("");
        String requestString = JsonUitl.objectToString(request);
        boolean temp = MyConnector.getInstance().sentMonitorCommand(requestString);

        if(temp) {
            msgReceived = MyConnector.getInstance().getMonitorMsg();
            if(!msgReceived.equals("")) {
                resolvingMonitoringData(msgReceived);
            } else {
                increasedProcesses.clear();
                decreasedProcesses.clear();
                setMemoryPercentArray(memoryPercentArray[DataNub - 1]);
                setCpuPercentArray(cpuPercentArray[DataNub - 1]);
            }
        } else {
            net_up = 0;
            net_down = 0;
            memory_used = 0;
            memory_total = 0;
            memory_available = 0;

            decreasedProcesses.clear();
            for (ProcessFormat process:currentProcesses){
                decreasedProcesses.add(process.getId());
            }

            for(int i = 0; i < DataNub; i++) {
                memoryPercentArray[i] = 0;
                cpuPercentArray[i] = 0;
            }
        }
    }

    /**
     * <summary>
     *  解析监控信息
     * </summary>
     * <param name="msgReceived">监控信息字符串</param>
     */
    private static void resolvingMonitoringData(String msgReceived) {
        Log.i("Monitor", msgReceived);
        ReceivedMonitorMessageFormat message = JsonUitl.stringToBean(msgReceived, ReceivedMonitorMessageFormat.class);
        if(message != null && message.getStatus() == 200) {
            MonitorData data = message.getData();
            net_up = data.getNet_up();
            net_down = data.getNet_down();
            memory_used = data.getMemory_used();
            memory_total = data.getMemory_total();
            memory_available = data.getMemory_available();

            setMemoryPercentArray((int)(memory_used / memory_total * 100));
            setCpuPercentArray(data.getCpu());

            currentProcesses = data.getProcesses();
            List<Integer> currentProcessesId = new ArrayList<>();
            for(ProcessFormat process:currentProcesses) {
                currentProcessesId.add(process.getId());
            }

            increasedProcesses.clear();
            for(ProcessFormat process:currentProcesses) {
                if(!processesId.contains(process.getId())) {
                    processesId.add(process.getId());
                    increasedProcesses.add(process);
                }
            }
            decreasedProcesses.clear();
            for(int i = 0; i < processesId.size(); i++) {
                if(!currentProcessesId.contains(processesId.get(i))) {
                    decreasedProcesses.add(processesId.get(i));
                    processesId.remove(i);
                }
            }
        }
    }

    /**
     * <summary>
     *  发送操作指令并获取执行状态
     * </summary>
     * <param name="name">操作名称</param>
     * <param name="command">操作</param>
     * <param name="param">参数</param>
     * <returns>执行状态</returns>
     */
    public static ReceivedActionMessageFormat getActionStateData(String name, String command, String param) {
        ReceivedActionMessageFormat message = new ReceivedActionMessageFormat();
        RequestFormat request = new RequestFormat();
        request.setName(name);
        request.setCommand(command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        switch (name) {
            case OrderConst.processAction_name: {
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                Log.i("Send2PCThread2", msgReceived);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedActionMessageFormat.class);
                }
            }
                break;
            case OrderConst.computerAction_name: {
                MyConnector.getInstance().getActionStateMsg(requestString);
            }
            break;

        }
        return message;
    }

    /**
     * <summary>
     *  通过进程ID获取进程当前信息
     * </summary>
     * <param name="id">进程ID</param>
     * <returns></returns>
     */
    public static ProcessFormat getProcessById(int id) {
        for(ProcessFormat process:currentProcesses) {
            if(process.getId() == id) {
                return process;
            }
        }
        return null;
    }

    /**
     * <summary>
     *  发送PC应用操作指令并获取执行状态
     * </summary>
     * <param name="name">操作名称</param>
     * <param name="command">操作</param>
     * <param name="param">参数</param>
     * <returns>执行结果</returns>
     */
    public static Object getExeActionStateData(String name, String command, String param) {
        Object message = new Object();

        RequestFormat request = new RequestFormat();
        request.setName(name);
        request.setCommand(command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        switch (command) {
            case OrderConst.appAction_get_command:
                message = getExeInforArray(request);
                break;
        }
        return message;
    }

    private static Object getExeInforArray(RequestFormat request) {
        List<ReceivedExeListFormat> receivedExeInforArray = new ArrayList<>();
        String requestMsg = JsonUitl.objectToString(request);

        ReceivedExeListFormat messageTemp = new ReceivedExeListFormat();
        String msgReceived = MyConnector.getInstance().getActionStateMsg(requestMsg);
        LogUtil.e("GETEXE", "首次发送" + requestMsg);
        LogUtil.e("GETEXE", "首次接收" + msgReceived);
        try{
            messageTemp = JsonUitl.stringToBean(msgReceived, ReceivedExeListFormat.class);
            receivedExeInforArray.add(messageTemp);
        } catch(Exception e) {}

        boolean signal = messageTemp.getStatus() == OrderConst.success &&
                messageTemp.getMessage().equals(OrderConst.exeAction_get_more_message);
        while(signal) {
            request.setParam(OrderConst.exeAction_get_more_param);
            requestMsg = JsonUitl.objectToString(request);
            msgReceived = MyConnector.getInstance().getActionStateMsg(requestMsg);
            Log.e("GETEXE", "发送获取更多" + requestMsg);
            Log.e("GETEXE", "接收获取更多" + msgReceived);
            try{
                messageTemp = JsonUitl.stringToBean(msgReceived, ReceivedExeListFormat.class);
                receivedExeInforArray.add(messageTemp);
            } catch(Exception e) {}

            signal = messageTemp.getStatus() == OrderConst.success &&
                    messageTemp.getMessage().equals(OrderConst.exeAction_get_more_message);
        }

        ReceivedExeListFormat allExeInfor = new ReceivedExeListFormat();
        List<ExeInformat> allExes = new ArrayList<>();
        for (ReceivedExeListFormat temp : receivedExeInforArray) {
            allExes.addAll(temp.getData());
        }
        int status = OrderConst.success;
        String message = null;
        if(allExes.size() == 0) {
            status = messageTemp.getStatus();
            message = messageTemp.getMessage();
        }
        allExeInfor.setStatus(status);
        allExeInfor.setMessage(message);
        allExeInfor.setData(allExes);

        return allExeInfor;
    }



}
