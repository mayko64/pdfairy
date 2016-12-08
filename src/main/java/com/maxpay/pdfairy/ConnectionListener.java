package com.maxpay.pdfairy;

import java.io.IOException;

public interface ConnectionListener {
    /**
     * Listen for incoming requests
     *
     * @param threads Number of threads to start
     * @throws IOException on input/output error
     */
    void listen(int threads) throws IOException;
}
