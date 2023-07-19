package com.despegar.javatemplate.config.thread.model;

public record BlockingQueueProperties(String type,
                                      ArrayBlockingQueueProperties array,
                                      BlockingArrayQueueProperties jettyArray,
                                      SynchronousQueueProperties synchronous,
                                      LinkedBlockingQueueProperties linked) {}

