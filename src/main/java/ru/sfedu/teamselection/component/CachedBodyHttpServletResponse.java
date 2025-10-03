package ru.sfedu.teamselection.component;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public final class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream cachedContent;
    private final ServletOutputStream outputStream;
    private final PrintWriter writer;
    private final String contentType;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
        this.cachedContent = new ByteArrayOutputStream();
        this.outputStream = new CachedServletOutputStream();
        this.writer = new PrintWriter(outputStream);
        this.contentType = response.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    public String getCachedContentAsString() {
        try {
            return cachedContent.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return cachedContent.toString();
        }
    }

    private final class CachedServletOutputStream extends ServletOutputStream {
        @Override
        public void write(int b) throws IOException {
            cachedContent.write(b);
            getResponse().getOutputStream().write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            // Not implemented
        }
    }
}
