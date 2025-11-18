package com.hayden.hap.controller;

import com.hayden.hap.dbop.entity.ReqParamVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.resp.ReturnResult;
import com.hayden.hap.common.resp.Status;
import com.hayden.hap.vo.MetaDataVO;
import com.hayden.hap.service.export.IExportMetaDataService;
import com.hayden.hap.serial.JsonUtils;
import com.hayden.hap.utils.MetaDataRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/metadata/export")
public class ExportMetaDataCtrl {

	@Autowired
	IExportMetaDataService exportMetaDataService;
	@Autowired
	MetaDataRelation metaDataRelation;
        @GetMapping(value="/getMetaDataType")
    public ReturnResult getMetaDataType(){
        ReturnResult returnResult=new ReturnResult();
        try {
            returnResult.setData(exportMetaDataService.getMetaDataType());
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }
	
        @GetMapping(value="/queryMetaData")
    public ReturnResult queryMetaData(@RequestParam String project, @RequestParam String env,@RequestParam String metaType,@RequestParam(required = false) String metaDataCode,ReqParamVO reqParam,@RequestParam String tenantid){
        ReturnResult returnResult=new ReturnResult();
        try {
            returnResult.setData(exportMetaDataService.queryMetaData(project, env, metaType, metaDataCode,reqParam,Long.valueOf(tenantid)));
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }
	
        @PostMapping(value="/exportMetaData")
    public void exportMetaData(HttpServletRequest request,
                        HttpServletResponse response) throws HDException{
		String project = request.getParameter("project");
		String env = request.getParameter("env");
		String metaType = request.getParameter("metaType");
		String data = request.getParameter("data");
		String tenantid = request.getParameter("tenantid");
		try {
			List<MetaDataVO> metaDatas = (List<MetaDataVO>) JsonUtils.parseArrayInit(data, MetaDataVO.class);
			if ("pcmenu".equals(metaType)||"mobilemenu".equals(metaType)) {
				Collections.sort(metaDatas, new Comparator<MetaDataVO>() {
					@Override
					public int compare(MetaDataVO o1, MetaDataVO o2) {
						return o1.getMetaDataCode().length()-o2.getMetaDataCode().length();
					}
				});
			}
			if ("table".equals(metaType)) {
				tenantid="1";
			}
			exportMetaDataService.exportMetaData(response,project, env, metaType,metaDatas,Long.valueOf(tenantid));
		} catch (HDException e) {
			handelException(request, response, e);
		}
	}
//	@RequestMapping(method = RequestMethod.GET,value="/metadata/export/getMetaDataRelation")
//    @ResponseBody
//    public ReturnResult getMetaDataRelation(String metaType){
//        ReturnResult returnResult=new ReturnResult();
//        try {
//            returnResult.setData(metaDataRelation.getMetaDataParentRelation(metaType));
//        } catch (Exception e) {
//            e.printStackTrace();
//            returnResult.setStatus(Status.FAIL);
//            returnResult.setMessage(e.getMessage());
//        }
//        return returnResult;
//    }
        @GetMapping(value="/exportFieldMetaData")
    public void exportFieldMetaData(HttpServletRequest request,
                        HttpServletResponse response) throws HDException{
		String project = request.getParameter("project");
		String env = request.getParameter("env");
		String table = request.getParameter("table");
		String fields = request.getParameter("fields");
		String where = request.getParameter("where");
		try {
			exportMetaDataService.exportFieldMetaData(response,project, env, table,fields,where);
		} catch (HDException e) {
			handelException(request, response, e);
		}
	}
        @GetMapping(value = "/getModuleList")
    public ReturnResult getProjects(@RequestParam String project, @RequestParam String env) {
        ReturnResult returnResult = new ReturnResult();
        try {
            returnResult.setData(exportMetaDataService.getModuListWithSync(project,env,1L));
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);

            e.printStackTrace();

            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            e.fillInStackTrace().printStackTrace(printWriter);
            returnResult.setMessage(result.toString());
        }
        return returnResult;
    }
	private void handelException(HttpServletRequest request, HttpServletResponse response, HDException e)
			throws HDException {
		response.reset();
		response.setStatus(200);
		// 设置响应给前台内容的数据格式 "application/json" text/plain
		response.setContentType("text/plain; charset=UTF-8");
		// response.setCharacterEncoding("UTF-8");
		// 设置响应给前台内容的PrintWriter对象
		PrintWriter printWriter = null;
		String showmsg = "";

		try {
			request.setCharacterEncoding("UTF-8");
			showmsg = new String(e.getMessage().getBytes("UTF-8"), "UTF-8");
		} catch (Exception e1) {
			throw new HDException(e1);
		}
		try {
			printWriter = response.getWriter();
			printWriter.print(showmsg);
		} catch (IOException e2) {
			throw new HDException(e2);
		} finally {
			if (null != printWriter) {
				printWriter.flush();
				printWriter.close();
			}
		}
	}

        @GetMapping(value = "/getMetaAndTenants")
        public ReturnResult getMetaAndTenants(@RequestParam String project, @RequestParam String env) {
		ReturnResult returnResult = new ReturnResult();
		try {
			returnResult.setData(exportMetaDataService.getMetaAndTenants(project,env));
		} catch (Exception e) {
			returnResult.setStatus(Status.FAIL);

			e.printStackTrace();

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.fillInStackTrace().printStackTrace(printWriter);
			returnResult.setMessage(result.toString());
		}
		return returnResult;
	}
}
