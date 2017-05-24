package com.bozhong.imanager.restful;

import com.bozhong.insist.zk.InsistZkClient;
import com.sun.jersey.spi.resource.Singleton;
import com.yx.eweb.main.EWebServletContext;
import org.apache.zookeeper.KeeperException;
import org.springframework.stereotype.Controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * Created by xiezg@317hu.com on 2017/5/24 0024.
 */
@Singleton
@Controller
@Path("insist")
public class InsistRest {

    /**
     * 请求参数：{
     * serviceName:服务名，
     * group:组
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("providerList")
    public String providerList(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String serviceName = (String) EWebServletContext.getEWebContext().get("serviceName");
        String group = (String) EWebServletContext.getEWebContext().get("group");
        return null;
    }

    /**
     * 请求参数：{
     * serviceName:"服务名",
     * group:组
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("consumerList")
    public String consumerList(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String serviceName = (String) EWebServletContext.getEWebContext().get("serviceName");
        String group = (String) EWebServletContext.getEWebContext().get("group");
        return null;
    }
}
