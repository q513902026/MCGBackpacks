package me.therealjeremy.jmp;

import java.net.*;

import org.bukkit.*;
import com.google.gson.*;

import java.io.*;

public class JMetrics extends Thread {
    private String userId;
    private String resourceId;
    private String downloadId;
    private long startTime;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JsonParser parser;
    private int failedAttempts;
    boolean running;

    public JMetrics(final String resourceId) {
        this.userId = "%%__USER__%%";
        this.resourceId = "%%__RESOURCE__%%";
        this.downloadId = "%%__NONCE__%%";
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.resourceId = resourceId;
        this.userId = "u";
        this.downloadId = "d";
    }

    public JMetrics() {
        this.userId = "%%__USER__%%";
        this.resourceId = "%%__RESOURCE__%%";
        this.downloadId = "%%__NONCE__%%";
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                this.socket = new Socket("therealjeremy.com", 7645);
                this.failedAttempts = 0;
                this.log("Connected to the JMetrics Server.");
                this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.out = new PrintWriter(this.socket.getOutputStream(), true);
                this.parser = new JsonParser();
                while (!this.socket.isClosed() && this.socket.isConnected()) {
                    final String input = this.in.readLine();
                    if (input == null) {
                        break;
                    }
                    JsonObject json;
                    try {
                        json = (JsonObject) this.parser.parse(input);
                    } catch (JsonParseException e3) {
                        this.log("There was an error parsing the JsonObject.");
                        continue;
                    }
                    if (json == null) {
                        continue;
                    }
                    if (json.get("request-info") == null) {
                        continue;
                    }
                    if (!json.get("request-info").getAsBoolean()) {
                        continue;
                    }
                    final JsonObject info = new JsonObject();
                    info.addProperty("server-id", Bukkit.getServerId());
                    info.addProperty("user-id", this.userId);
                    info.addProperty("resource-id", this.resourceId);
                    info.addProperty("download-id", this.downloadId);
                    info.addProperty("start-time", (Number) this.startTime);
                    info.addProperty("player-count", (Number) Bukkit.getOnlinePlayers().size());
                    final JsonObject reply = new JsonObject();
                    reply.add("server-info", (JsonElement) info);
                    this.out.println(reply.toString());
                }
            } catch (IOException e) {
                final String message = e.getMessage();
                this.log("Error: " + message);
                if (message.equals("Socket closed")) {
                    return;
                }
                if (message.equals("Connection refused: connect")) {
                    final int secToRetry = (this.failedAttempts < 2) ? 5 : ((this.failedAttempts < 5) ? 60 : 300);
                    ++this.failedAttempts;
                    this.log("Will retry in " + secToRetry + " seconds.");
                    try {
                        Thread.sleep(secToRetry * 1000);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        if (this.out != null) {
                            this.out.close();
                        }
                        if (this.in != null) {
                            this.in.close();
                        }
                        if (this.socket != null) {
                            this.socket.close();
                        }
                        try {
                            if (this.out != null) {
                                this.out.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                            if (this.socket == null) {
                                try {
                                    if (this.out != null) {
                                        this.out.close();
                                    }
                                    if (this.in != null) {
                                        this.in.close();
                                    }
                                    if (this.socket == null) {
                                        continue;
                                    }
                                    this.socket.close();
                                } catch (IOException | NullPointerException ex30) {
                                    ex30.printStackTrace();
                                }
                                continue;
                            }
                            this.socket.close();
                        } catch (IOException | NullPointerException ex31) {
                            ex31.printStackTrace();
                        }
                        try {
                            if (this.out != null) {
                                this.out.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                            if (this.socket == null) {
                                continue;
                            }
                            this.socket.close();
                        } catch (IOException | NullPointerException ex32) {
                            ex32.printStackTrace();
                        }
                        continue;
                    } catch (IOException | NullPointerException ex33) {
                        ex33.printStackTrace();
                        try {
                            if (this.out != null) {
                                this.out.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                            if (this.socket == null) {
                                try {
                                    if (this.out != null) {
                                        this.out.close();
                                    }
                                    if (this.in != null) {
                                        this.in.close();
                                    }
                                    if (this.socket == null) {
                                        continue;
                                    }
                                    this.socket.close();
                                } catch (IOException | NullPointerException ex34) {
                                    ex34.printStackTrace();
                                }
                                continue;
                            }
                            this.socket.close();
                        } catch (IOException | NullPointerException ex35) {
                            ex35.printStackTrace();
                        }
                        try {
                            if (this.out != null) {
                                this.out.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                            if (this.socket == null) {
                                continue;
                            }
                            this.socket.close();
                        } catch (IOException | NullPointerException ex36) {
                            ex36.printStackTrace();
                        }
                        continue;
                    }
                }
                e.printStackTrace();
                try {
                    if (this.out != null) {
                        this.out.close();
                    }
                    if (this.in != null) {
                        this.in.close();
                    }
                    if (this.socket == null) {
                        try {
                            if (this.out != null) {
                                this.out.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                            if (this.socket == null) {
                                continue;
                            }
                            this.socket.close();
                        } catch (IOException | NullPointerException ex37) {
                            ex37.printStackTrace();
                        }
                        continue;
                    }
                    this.socket.close();
                } catch (IOException | NullPointerException ex38) {
                    ex38.printStackTrace();
                }
                try {
                    if (this.out != null) {
                        this.out.close();
                    }
                    if (this.in != null) {
                        this.in.close();
                    }
                    if (this.socket == null) {
                        continue;
                    }
                    this.socket.close();
                } catch (IOException | NullPointerException ex39) {
                    ex39.printStackTrace();
                }
            } finally {
                try {
                    if (this.out != null) {
                        this.out.close();
                    }
                    if (this.in != null) {
                        this.in.close();
                    }
                    if (this.socket != null) {
                        this.socket.close();
                    }
                } catch (IOException | NullPointerException ex40) {
                    ex40.printStackTrace();
                }
            }
        }
    }

    public void stopMetrics() {
        this.running = false;
        try {
            if (this.out != null) {
                this.out.close();
            }
            if (this.in != null) {
                this.in.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(final Object o) {
        System.out.println("[JMetrics] " + o);
    }
}
