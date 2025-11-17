package com.hayden.hap.common.utils.gzip;

import com.tacitknowledge.filters.GenericFilter;
import com.tacitknowledge.filters.GenericResponseWrapper;
import org.apache.commons.lang.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 参考 com.tacitknowledge.filters.gzipfilter.GZIPFilter
 * 
 * 增加以 /static 开头的过滤
 * 
 * @author lengzy
 * @date 2016年5月5日
 */
public class GZIPFilter extends GenericFilter
{
    /** The header for accepted encodings */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    
    /** The header for content encodings */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    
    /**
     * Filters the response by wrapping the output stream, by checking
     * to see if the browser will accept gzip encodings, and if they
     * can, temporarily storing all of the response data and gzipping
     * it before finally sending it to the client
     *
     * @param request the request to filter
     * @param response the response to filter
     * @param chain the FilterChain to participate in
     * @exception IOException if there is a problem writing the data to the client
     * @exception ServletException for servlet problems
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                 FilterChain chain) throws IOException, ServletException
    {
    	
        if (isEnabled() && isFilterPath(request))
        {
            // Make sure that proxies know that content will be returned
            // differently based on the Accept-Encoding request header; this
            // prevents them from caching the gzip'd response and returning it
            // to non-gzip-aware clients
            response.addHeader("Vary", ACCEPT_ENCODING);
            
            // Create a response wrapper that captures servlet output
            // instead of sending it to the client
            GenericResponseWrapper wrapper = new GenericResponseWrapper(response);

            // Let the server perform any work that it must perform,
            // then get the output back
            chain.doFilter(request, wrapper);
            OutputStream out = response.getOutputStream();

            // If the content isn't cached, its not an internal forward, and the
            // browser accepts gzip, compress the thing and send it out
            if (!isCached(wrapper) && !isIncluded(request) && acceptsGzip(request))
            {
                byte[] compressedData = compressData(wrapper.getData());
                response.setHeader(CONTENT_ENCODING, "gzip");
                response.setContentLength(compressedData.length);
                out.write(compressedData);
            }
            else
            {
                // we can't compress this data, just write it straight through
                out.write(wrapper.getData());
            }
            
            // make sure all the data made it to the client
            out.flush();
            out.close();
        }
        else
        {
            chain.doFilter(request, response);
        }
    }
    
    /**
     * 判断请求路径是否以 /static 开头
     * @param request
     * @return 
     * @author lengzy
     * @date 2016年5月5日
     */
    private boolean isFilterPath(HttpServletRequest request) {
    	String servletPath = request.getServletPath();
    	if (StringUtils.startsWith(servletPath, "/static")) {
    		return true;
    	}
    	
    	//对所有json请求进行压缩
    	String contentType = request.getParameter("contentType");
    	if("json".equals(contentType)) {
    		return true;
    	}
    	
    	/**
    	 * add by wusy  2016-05-27 13:30:00
    	 * 用于对权限树返回的json进行压缩，以后需优化为读取配置文件
    	 */
    	if (StringUtils.startsWith(servletPath, "/sy/SY_PERMISSON_PC/getPermissionTree")) {
    		return true;
    	}
    	  	
		return false;
	}

	/** {@inheritDoc} */
    @Override
    public void printBanner(ServletContext context)
    {
        context.log("$Id: GZIPFilter.java,v 1.15 2005/03/12 01:52:29 mike Exp $");
        context.log("\tGZIPFilter.Enabled: " + isEnabled());
        context.log("\tGZIPFilter.LogStats: "
                    + getBooleanConfigEntry("GZIPFilter.LogStats"));
    }

    /**
     * Takes a byte array, compresses it, and writes it to the given output stream
     *
     * @param data the data to compress
     * @return byte[] containing the compressed data
     * @exception IOException if there was a problem writing to the stream
     */
    protected byte[] compressData(byte[] data) throws IOException
    {
        // do the compression
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        GZIPOutputStream gzout = new GZIPOutputStream(compressed);
        gzout.write(data);
        gzout.flush();
        gzout.close();
        
        // If statistics are desired, compute them and log them out
        if (getBooleanConfigEntry("GZIPFilter.LogStats"))
        {
            StringBuffer stats = new StringBuffer("" + data.length);
            stats.append(" / " + compressed.size());
            stats.append(" / " + (data.length - compressed.size()));
            
            if (data.length > 0)
            {
                double doubleRatio = (double) compressed.size() / (double) data.length;
                int ratio = (int) (doubleRatio * 100);
                stats.append(" / " + ratio + "%");
            }
            else
            {
                stats.append(" / NaN");
            }
            
            getFilterConfig().getServletContext().log(
                "GZIPFilter: Original / GZip / Saved / Ratio: " + stats);
        }
        
        return compressed.toByteArray();
    }
    
    /**
     * Make sure that the request isn't actually an internal redirect,
     * gzipping content as it bashed around inside the server isn't a good idea
     * 
     * @param request the request to check for internal referencing
     * @return true if the request is included
     */
    protected boolean isIncluded(ServletRequest request)
    {
        String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null)
        {
            return false;
        }
        
        return true;
    }

    /**
     * If the request content is empty, it makes no sense to gzip
     * 
     * @param wrapper the response wrapper
     * @return boolean true if the response has no data in it
     */
    protected boolean isCached(GenericResponseWrapper wrapper)
    {
        if (wrapper.getData().length > 0)
        {
            return false;
        }

        return true;
    }
    
    /**
     * Determines if the filter is enabled.  This implementation looks for the
     * <code>GZIPFilter.Enabled</code> property in the tk-filters.properties
     * file.
     * 
     * @return <code>true</code> if the filter is enabled
     */
    @Override
    protected boolean isEnabled()
    {
        return getBooleanConfigEntry("GZIPFilter.Enabled");
    }

    /**
     * Examine the request to make sure that the browser can accept
     * gzip encodings
     * 
     * @param request the request to examine for gzip acceptance
     * @return boolean true if the request can accept gzip
     */
    protected boolean acceptsGzip(HttpServletRequest request)
    {
        return headerContains(request, ACCEPT_ENCODING, "gzip");
    }
}
