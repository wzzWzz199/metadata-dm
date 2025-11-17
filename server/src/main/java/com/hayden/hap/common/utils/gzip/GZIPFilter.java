package com.hayden.hap.common.utils.gzip;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

/**
 * Simplified gzip filter used to compress large JSON/static responses served by the upgrade console.
 */
public class GZIPFilter extends OncePerRequestFilter {

    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String CONTENT_ENCODING = "Content-Encoding";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!shouldCompress(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        BufferedResponseWrapper responseWrapper = new BufferedResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        byte[] body = responseWrapper.toByteArray();
        if (body.length == 0 || !acceptsGzip(request) || isIncluded(request)) {
            responseWrapper.copyBodyToResponse();
            return;
        }

        response.setHeader("Vary", ACCEPT_ENCODING);
        response.setHeader(CONTENT_ENCODING, "gzip");
        response.setContentLength(-1);
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream())) {
            gzipOutputStream.write(body);
        }
    }

    private boolean shouldCompress(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (StringUtils.startsWith(servletPath, "/static")) {
            return true;
        }
        String contentType = request.getParameter("contentType");
        if ("json".equals(contentType)) {
            return true;
        }
        return StringUtils.startsWith(servletPath, "/sy/SY_PERMISSON_PC/getPermissionTree");
    }

    private boolean acceptsGzip(HttpServletRequest request) {
        String header = request.getHeader(ACCEPT_ENCODING);
        return header != null && header.contains("gzip");
    }

    private boolean isIncluded(HttpServletRequest request) {
        return request.getAttribute("jakarta.servlet.include.request_uri") != null;
    }

    private static class BufferedResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private final ServletOutputStream outputStream = new BufferingServletOutputStream(buffer);
        private PrintWriter writer;

        BufferedResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
            }
            return writer;
        }

        byte[] toByteArray() throws IOException {
            if (writer != null) {
                writer.flush();
            }
            return buffer.toByteArray();
        }

        void copyBodyToResponse() throws IOException {
            HttpServletResponse response = (HttpServletResponse) getResponse();
            if (writer != null) {
                writer.flush();
            }
            response.getOutputStream().write(buffer.toByteArray());
        }
    }

    private static class BufferingServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream buffer;

        BufferingServletOutputStream(ByteArrayOutputStream buffer) {
            this.buffer = buffer;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // not used
        }

        @Override
        public void write(int b) {
            buffer.write(b);
        }
    }
}
