package com.p2p.shake;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class ShakeThread extends Thread {
    public static final int DEFAULT_PORT = 8899;
    public static final int RECEIVE_IPC_INFO = 0;
    public static final int CLOSE_SERVER = 999;
    public int SEND_TIMES;

    private int port;
    private boolean isRun;

    private DatagramSocket server;
    private DatagramSocket broadcast;
    private Selector selector;
    private DatagramChannel channel;
    private Handler handler;

    private InetAddress host;
    public ShakeThread(Handler handler) {
        this.port = DEFAULT_PORT;
        this.SEND_TIMES = 10;
    }

    public void setSearchTime(long time) {
        SEND_TIMES = (int) (time / 1000);
    }

    public void setInetAddress(InetAddress host) {
        this.host = host;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        isRun = true;
        try {
            selector = Selector.open();
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            server = channel.socket();
            server.bind(new InetSocketAddress(port));
            channel.register(selector, SelectionKey.OP_READ);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

            new Thread() {
                @Override
                public void run() {
                    try {
                        int times = 0;
                        broadcast = new DatagramSocket();
                        broadcast.setBroadcast(true);
                        while (times < SEND_TIMES) {
                            if (!isRun) {
                                return;
                            }
                            times++;
                            Log.e("my", "shake thread send broadcast.");

                            ShakeData data = new ShakeData();
                            data.setCmd(ShakeData.Cmd.GET_DEVICE_LIST);
                            // data.setName("hello");
                            // data.setLeftlength(ShakeData.DEFAULT_LEFT_LENGTH);
                            // data.setRightlength(0);
                            // data.setError_code(0);
                            // data.setFlag(1);
                            // data.setId(11126);

                            // data.setType(1);
                            // Log.e("my","************************************");
                            // byte[] b = ShakeData.getBytes(data);
                            // int m = 0;
                            // String mmm = "";
                            // for(int i=512;i<b.length;i++){
                            //
                            // mmm += b[i]+" ";
                            // m++;
                            // if(m==64){
                            // m=0;
                            // Log.e("my",mmm);
                            // mmm = "";
                            // }
                            // }
//                            byte[] b;
//                            if(times%2==0){
//                            	b=new byte[1];
//                            }else{
//                            	b=new byte[100];
//                            }
//                            b[0]=1;
//                            DatagramPacket packet = new DatagramPacket(
//       							b, b.length,
//       							 InetAddress.getByName("255.255.255.255"),
//       							 port); 
                            
							 DatagramPacket packet = new DatagramPacket(
							 ShakeData.getBytes(data), 100,
							 InetAddress.getByName("255.255.255.255"),
							 port); 
							broadcast.send(packet);
                            Thread.sleep(1000);
                        }

                        Log.e("my", "shake thread broadcast end.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            broadcast.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ShakeManager.getInstance().stopShaking();
                    }
                }
            }.start();

            while (isRun) {

                int n = selector.select(100);
                if (n > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        keys.remove(key);
                        if (key.isReadable()) {
                            DatagramChannel dc = (DatagramChannel) key.channel();
                            InetSocketAddress client = (InetSocketAddress) dc.receive(receiveBuffer);
                            key.interestOps(SelectionKey.OP_READ);
                            receiveBuffer.flip();
                            // Log.e("my",client.getAddress().toString());
                            // if(!client.getAddress().toString().equals("/192.168.1.87")){
                            // Log.e("my","************************************");
                            // byte[] b = receiveBuffer.array();
                            // int m = 0;
                            // String mmm = "";
                            // for(int i=0;i<b.length;i++){
                            //
                            // mmm += b[i]+" ";
                            // m++;
                            // if(m==64){
                            // m=0;
                            // Log.e("my",mmm);
                            // mmm = "";
                            // }
                            // }
                            // }
                            ShakeData data = ShakeData.getShakeData(receiveBuffer);

                            switch (data.getCmd()) {
                                case ShakeData.Cmd.RECEIVE_DEVICE_LIST:

                                    // Log.e("my",data.getError_code()+"");
                                    // Log.e("my",data.getFlag()+"");
                                    // Log.e("my",data.getId()+"");
                                    // Log.e("my",data.getLeftlength()+"");
                                    // Log.e("my",data.getRightCount()+"");
                                    // Log.e("my",data.getType()+"");

								if (data.getError_code() == 1) {
									if (null != handler) {
										Message msg = new Message();
										msg.what = ShakeManager.HANDLE_ID_RECEIVE_DEVICE_INFO;
										Bundle bundle = new Bundle();
										bundle.putSerializable("address", client.getAddress());
										bundle.putString("id", data.getId() + "");
										bundle.putString("name", data.getName() + "");
										bundle.putInt("flag", data.getFlag());
										bundle.putInt("type", data.getType());
										bundle.putInt("rtspflag", data.getRightCount());
										bundle.putInt("subType", data.getSubType());
										msg.setData(bundle);
										handler.sendMessage(msg);
									}  
                                    break;
                                }else{
                                }
                            // String recvStr =
                            // Charset.forName("utf-8").newDecoder().decode(receiveBuffer).toString();

                            receiveBuffer.clear();
                        }
                    }
                }

            }
           }
            Log.e("my", "shake thread end.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ShakeManager.getInstance().stopShaking();

            if (null != handler) {
                Message msg = new Message();
                msg.what = ShakeManager.HANDLE_ID_SEARCH_END;
                handler.sendMessage(msg);
            }

            try {
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void killThread() {
        if (isRun) {
            selector.wakeup();
            isRun = false;
        }
    }

}
