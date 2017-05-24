package com.bozhong.imanager.restful;

import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.common.util.StringUtil;
import com.bozhong.imanager.common.ImanagerErrorEnum;
import com.bozhong.imanager.tools.ImanagerUtil;
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
        //分组
        List<String> groupPaths = getChildrenPath(InsistUtil.getProviderZkPath());
        if (CollectionUtils.isEmpty(groupPaths)) {
            return ResultMessageBuilder.build(false, ImanagerErrorEnum.E10002.getError(),
                    ImanagerErrorEnum.E10002.getMsg()).
                    toJSONString();
        }

        //查询条件分组不为空时
        if (StringUtil.isNotBlank(group)) {
            groupPaths = new ArrayList<>();
            groupPaths.add(group.trim());
        }

        for (String groupPath : groupPaths) {
            //服务
            List<String> serviceGroupPaths = getChildrenPath(ImanagerUtil.getProviderGroupPath(groupPath));
            if (CollectionUtils.isEmpty(serviceGroupPaths)) {
                continue;
            }

            //查询条件服务名不为空时
            if (StringUtil.isNotBlank(serviceName)) {
                serviceGroupPaths = new ArrayList<>();
                serviceGroupPaths.add(serviceName.trim());
            }

            for (String serviceGroupPath : serviceGroupPaths) {
                //版本
                List<String> versionServiceGroupPaths = getChildrenPath(ImanagerUtil.
                        getProviderGroupServiceNamePath(groupPath, serviceGroupPath));
                if (CollectionUtils.isEmpty(versionServiceGroupPaths)) {
                    continue;
                }

                for (String versionServiceGroupPath : versionServiceGroupPaths) {
                    //IP端口号
                    List<String> versionServiceGroupPathAndIpPorts = getChildrenPath(ImanagerUtil.
                            getServiceNameGroupVersionZkPath(groupPath, serviceGroupPath, versionServiceGroupPath));
                    if (CollectionUtils.isEmpty(versionServiceGroupPathAndIpPorts)) {
                        continue;
                    }

                    //节点数据
                    for (String versionServiceGroupPathAndIpPort :
                            versionServiceGroupPathAndIpPorts) {
                        String serviceMetaStr = getPathData(ImanagerUtil.getIpPortPath(groupPath, serviceGroupPath,
                                versionServiceGroupPath, versionServiceGroupPathAndIpPort));
                        if (StringUtil.isNotBlank(serviceMetaStr)) {
                            serviceMetaList.add(InsistUtil.jsonToServiceMeta(serviceMetaStr));
                        }

                    }

                }

            }

        }

        return ResultMessageBuilder.build(serviceMetaList).toJSONString();
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

    /**
     * 获取子节点
     *
     * @param baseZkPath
     * @return
     */
    private List<String> getChildrenPath(String baseZkPath) {
        try {
            return InsistZkClient.getInstance().getNodeChildren(baseZkPath);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取节点数据
     *
     * @param zkPath
     * @return
     */
    private String getPathData(String zkPath) {
        try {
            return InsistZkClient.getInstance().getDataForStr(zkPath, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
