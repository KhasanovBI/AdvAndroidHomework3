package com.technopark.bulat.advandroidhomework3.network;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.network.request.RequestMessage;

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
 * Created by bulat on 21.01.16.
 */
public class SocketClient implements SocketParams {
    private static final String LOG_TAG = "MySocketClient";
    private static final int CHUNK_SIZE = 16384;
    private static Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SocketCallback socketCallback;
    private boolean isNeedRun = false;

    public SocketClient(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
        runResponseGetter();
    }

    public void runResponseGetter() {
        isNeedRun = true;
        Thread responseGetter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isNeedRun) {
                    List<JSONObject> responseJSONObjectArray = getResponses();
                    if (responseJSONObjectArray != null) {
                        for (JSONObject responseJSONObject : responseJSONObjectArray) {
                            socketCallback.send(new SocketResponseMessage(responseJSONObject));
                        }
                        threadSleep(SOCKET_CHECK_TIME);
                    }
                }
            }
        });
        responseGetter.start();
    }

    public void stopResponseGetter() {
        isNeedRun = false;
    }

    private void threadSleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean connect() {
        Log.d(LOG_TAG, "connect");
        int connectionErrorCode;
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
            return true;
        } catch (ConnectException e) {
            connectionErrorCode = 0;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            connectionErrorCode = 1;
            e.printStackTrace();
        } catch (SocketException e) {
            connectionErrorCode = 2;
            e.printStackTrace();
        } catch (IOException e) {
            connectionErrorCode = 3;
            e.printStackTrace();
        }
        socketCallback.send(new SocketResponseMessage(connectionErrorCode));
        return false;
    }

    private boolean checkConnection() {
        return socket != null && socket.isConnected() || connect();
    }

    private List<JSONObject> getResponses() {
        String socketOutputString = null;
        if (checkConnection()) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] data = new byte[CHUNK_SIZE];
                while (true) {
                    int readBytesCount;
                    try {
                        readBytesCount = inputStream.read(data, 0, data.length);
                    } catch (SocketTimeoutException e) {
                        break;
                    }
                    if (readBytesCount > 0) {
                        // Что-то прочитано из сокета
                        outputStream.write(data, 0, readBytesCount);
                    } else {
                        if (readBytesCount == 0) {
                            // Закончил читать
                            break;
                        } else {
                            // Ошибка, сокет отключился
                            if (!connect()) {
                                return null;
                            }
                        }
                    }
                    try {
                        // Пытаемся прочитать хотя бы один валидный JSON, иначе опять идем по циклу.
                        new JSONObject(outputStream.toString("utf-8"));
                        break;
                    } catch (JSONException ignored) {
                    }
                }
                socketOutputString = outputStream.toString("utf-8");
                if (socketOutputString.equals("")) {
                    socketOutputString = null;
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parseSocketOutput(socketOutputString);
    }

    private List<JSONObject> parseSocketOutput(String socketOutputString) {
        List<JSONObject> jsonResponses = null;
        if (socketOutputString != null && socketOutputString.length() > 0) {
            jsonResponses = new ArrayList<>();
            try {
                do {
                    /* Из socket может быть прочитано более 1 строки. */
                    JSONObject splitResponseJson = new JSONObject(socketOutputString);
                    int splitResponseStringLength = splitResponseJson.toString().length();
                    jsonResponses.add(splitResponseJson);
                    socketOutputString = socketOutputString.substring(splitResponseStringLength);
                } while (socketOutputString.length() > 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonResponses;
    }

    public void performRequest(RequestMessage requestMessage) {
        String requestString = requestMessage.getRequestString();
        Log.d(LOG_TAG, "Request: " + requestString);
        if (checkConnection()) {
            try {
                outputStream.write(requestString.getBytes(Charset.forName("UTF-8")));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
