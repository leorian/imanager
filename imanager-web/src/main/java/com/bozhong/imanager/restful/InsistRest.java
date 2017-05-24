package com.bozhong.imanager.restful;

import com.alibaba.fastjson.JSON;
import com.bozhong.common.util.StringUtil;
import com.bozhong.insist.common.InsistConstants;
import com.bozhong.insist.common.InsistUtil;
import com.bozhong.insist.module.ServiceMeta;
import com.bozhong.insist.zk.InsistZkClient;
import com.sun.jersey.spi.resource.Singleton;
import com.yx.eweb.main.EWebServletContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

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
        List<ServiceMeta> serviceMetaList = new ArrayList<>();
        if (StringUtil.isBlank(serviceName) && StringUtil.isBlank(group)) {
            try {
                List<String> groupPaths = InsistZkClient.getInstance().getNodeChildren(InsistUtil.getProviderZkPath());
                if (!CollectionUtils.isEmpty(groupPaths)) {
                    for (String groupPath : groupPaths) {
                        List<String> serviceGroupPaths = InsistZkClient.getInstance().
                                getNodeChildren(InsistUtil.getProviderZkPath() +
                                        InsistConstants.INSIST_ZK_SLASH + groupPath);
                        if (!CollectionUtils.isEmpty(serviceGroupPaths)) {
                            for (String serviceGroupPath : serviceGroupPaths) {
                                List<String> versionServiceGroupPaths = InsistZkClient.getInstance().
                                        getNodeChildren(InsistUtil.getProviderZkPath() +
                                                InsistConstants.INSIST_ZK_SLASH + groupPath +
                                                InsistConstants.INSIST_ZK_SLASH + serviceGroupPath);
                                if (!CollectionUtils.isEmpty(versionServiceGroupPaths)) {
                                    for (String versionServiceGroupPath : versionServiceGroupPaths) {
                                        List<String> versionServiceGroupPathAndIpPorts = InsistZkClient.
                                                getInstance().getNodeChildren(InsistUtil.getProviderZkPath() +
                                                InsistConstants.INSIST_ZK_SLASH + groupPath +
                                                InsistConstants.INSIST_ZK_SLASH + serviceGroupPath +
                                                InsistConstants.INSIST_ZK_SLASH + versionServiceGroupPath);
                                        if (!CollectionUtils.isEmpty(versionServiceGroupPathAndIpPorts)) {
                                            for (String versionServiceGroupPathAndIpPort :
                                                    versionServiceGroupPathAndIpPorts) {
                                                String serviceMetaStr = InsistZkClient.getInstance().getDataForStr(InsistUtil.getProviderZkPath() +
                                                        InsistConstants.INSIST_ZK_SLASH + groupPath +
                                                        InsistConstants.INSIST_ZK_SLASH + serviceGroupPath +
                                                        InsistConstants.INSIST_ZK_SLASH + versionServiceGroupPath +
                                                        InsistConstants.INSIST_ZK_SLASH + versionServiceGroupPathAndIpPort, -1);
                                                if (StringUtil.isNotBlank(serviceMetaStr)) {
                                                    serviceMetaList.add(InsistUtil.jsonToServiceMeta(serviceMetaStr));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        return JSON.toJSONString(serviceMetaList);
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

    /**
     * 获取zookeeper连接信息
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("zkHosts")
    public String zkHosts(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        return System.getProperty("insist.zkHosts");
    }
}
