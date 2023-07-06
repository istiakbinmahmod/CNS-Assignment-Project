package net.javaguides.springboot.UtilController;

import net.javaguides.springboot.service.ProjectService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class JasperController {

    @Autowired
    private ProjectService projectService;

//    @GetMapping("/report/{format}")
//    public String generateReport(@PathVariable String format, @RequestParam("filterStartDate") Date start, @RequestParam("filterStartDate") Date end) throws FileNotFoundException, JRException {
//        return projectService.exportReport(format, start, end);
//    }
    @GetMapping("/report/{format}")
    public String generateReport(@PathVariable String format,
                                 @RequestParam(value = "filterStartDate", required = false) String startDateStr,
                                 @RequestParam(value = "filterEndDate", required = false) String endDateStr)
            throws FileNotFoundException, JRException {
        Date startDate = null;
        Date endDate = null;

        // Convert startDateStr and endDateStr to Date objects
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
            } catch (ParseException e) {
                // Handle the parsing exception if needed
            }
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
            } catch (ParseException e) {
                // Handle the parsing exception if needed
            }
        }

        return projectService.exportReport(format, startDate, endDate);
    }


}
