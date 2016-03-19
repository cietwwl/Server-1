package com.dx.gods.common.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dx.gods.service.fileupload.ResourceContainer;

/**
 * Servlet implementation class ResourceMgs
 * 请求资源更新的入口
 */
public class ResourceMgr extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResourceMgr() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String clientSeqType = request.getParameter("clientSeqType");
		String mainVersionNo = request.getParameter("mainVersionNo");
		String subVersionNo = request.getParameter("subVersionNo");
		String versionTime = request.getParameter("versionTime");
		
		
		ResourceContainer.processClientResUpdateRequest(response, mainVersionNo, subVersionNo, versionTime, clientSeqType);
	}

}
