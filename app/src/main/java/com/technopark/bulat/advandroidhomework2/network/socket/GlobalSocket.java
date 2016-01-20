package com.technopark.bulat.advandroidhomework2.network.socket;

import android.util.Log;

import com.technopark.bulat.advandroidhomework2.network.request.RequestMessage;
import com.technopark.bulat.advandroidhomework2.network.response.RawResponse;
import com.technopark.bulat.advandroidhomework2.network.response.messages.WelcomeResponse;
import com.technopark.bulat.advandroidhomework2.network.socket.socketObserver.Observable;
import com.technopark.bulat.advandroidhomework2.network.socket.socketObserver.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bulat on 11.11.15.
 */
public class GlobalSocket implements SocketParams, Observable {
    private final List<Observer> observers = new ArrayList<>();
    private static volatile GlobalSocket instance;
    private static final String LOG_TAG = "GlobalSocket";
    private static Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Thread asyncThread;

    public static GlobalSocket getInstance() {
        GlobalSocket localInstance = instance;
        if (localInstance == null) {
            synchronized (GlobalSocket.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new GlobalSocket();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void registerObserver(Observer o) {
        synchronized (this) {
            if (!observers.contains(o)) {
                observers.add(o);
            }
        }
    }

    @Override
    public void removeObserver(Observer o) {
        synchronized (this) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(RawResponse rawResponse) {
        synchronized (this) {
            for (Observer observer : observers) {
                observer.handleResponseMessage(rawResponse);
            }
        }
    }

    private GlobalSocket() {
        turnOnAsyncThread();
    }

    public void turnOnAsyncThread() {
        asyncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String responseString;
                    try {
                        Thread.sleep(SOCKET_CHECK_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    responseString = readInputStream();
                    if (responseString != null) {
                        try {
                            /* Когда из socket'а прочитано более 1 строки. */
                            while (responseString.length() > 0) {
                                JSONObject splitResponseJson = new JSONObject(responseString);
                                int splitResponseStringLength = splitResponseJson.toString().length();
                                RawResponse rawResponse = getRawResponse(splitResponseJson);
                                if (rawResponse != null) {
                                    notifyObservers(rawResponse);
                                }
                                responseString = responseString.substring(splitResponseStringLength);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        asyncThread.start();
    }

    private int connect() {
        Log.d(LOG_TAG, "connect");
        try {
            if (socket != null) {
                socket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(SOCKET_READ_KEEPALIVE);
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = socket.getOutputStream();
        } catch (ConnectException e) {
            e.printStackTrace();
            notifyObservers(getErrorRawResponse(0));
            return -1;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            notifyObservers(getErrorRawResponse(1));
            return -1;
        } catch (SocketException e) {
            e.printStackTrace();
            notifyObservers(getErrorRawResponse(2));
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            notifyObservers(getErrorRawResponse(3));
            return -1;
        }
        return 0;
    }

    private RawResponse getErrorRawResponse(int errorCode) {
        JSONObject error = new JSONObject();
        try {
            error.put("Message", errorCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RawResponse("error", error);
    }

    private String readInputStream() {
        if (socket == null || !socket.isConnected()) {
            if (connect() == -1) {
                return "";
            }
        }
        String output = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int read;
            byte[] data = new byte[16384];
            while (true) {
                try {
                    read = inputStream.read(data, 0, data.length);
                } catch (SocketTimeoutException e) {
                    break;
                }
                if (read > 0) {
                    outputStream.write(data, 0, read);
                } else {
                    if (read == 0) {
                        break;
                    } else {
                        if (connect() == -1) {
                            return "";
                        }
                    }
                }
                try {
                    JSONObject jsonObject = new JSONObject(outputStream.toString("utf-8"));
                    break;
                } catch (JSONException ignored) {
                }

            }
            output = outputStream.toString("utf-8");
            if (output.equals("")) {
                output = null;
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    private class Request extends Thread {
        private final RequestMessage requestMessage;

        Request(RequestMessage requestMessage) {
            this.requestMessage = requestMessage;
        }

        @Override
        public void run() {
            String requestString = requestMessage.getRequestString();
            Log.d(LOG_TAG, "Request: " + requestString);
            if (socket == null || !socket.isConnected()) {
                if (connect() == -1) {
                    return;
                }
            }
            try {
                outputStream.write(requestString.getBytes(Charset.forName("UTF-8")));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void performAsyncRequest(RequestMessage requestMessage) {
        (new Request(requestMessage)).start();
    }

    private RawResponse getRawResponse(JSONObject splitResponseJson) {
        try {
            String action = splitResponseJson.getString("action");
            JSONObject jsonData;
            if (!action.equals("welcome")) {
                jsonData = splitResponseJson.getJSONObject("data");
                return new RawResponse(action, jsonData);
            } else {
                WelcomeResponse welcomeResponse = new WelcomeResponse(splitResponseJson);
                return null;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}