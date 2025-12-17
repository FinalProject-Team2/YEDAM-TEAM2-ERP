package store.yd2team.common.web;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
public class PdfController {

	@Autowired DataSource datasource;
	
	@RequestMapping("/pdf")
	 public void report(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = datasource.getConnection();
		   String filename = getClass().getResource("/static/report/attd.jasper").getFile(); 
		   JasperPrint jasperPrint = JasperFillManager.fillReport(filename,  null, conn);
		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
	 }
	
	@RequestMapping("/jumunseo")
	 public void reportJumunseo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = datasource.getConnection();
		   String filename = getClass().getResource("/static/report/jumunseo.jasper").getFile(); 
		   Map<String, Object> params = new HashMap<>();
		   params.put("so_id", "SO2512170013");//

		   JasperPrint jasperPrint = JasperFillManager.fillReport(filename,  params, conn);
		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
	 }
}
