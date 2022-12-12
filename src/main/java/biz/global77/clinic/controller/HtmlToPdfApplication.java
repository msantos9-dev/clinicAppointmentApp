package biz.global77.clinic.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.MedicalCertificate;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.MedicalCertificateRepository;

@Controller
@RequestMapping("/doctor")
public class HtmlToPdfApplication {

    @Autowired
    private MedicalCertificateRepository certRepo;

    public static void main(String[] args) {
        SpringApplication.run(HtmlToPdfApplication.class, args);
    }

    @GetMapping("/genpdf/{fileName}")
    HttpEntity<byte[]> createPdf(
            @PathVariable("fileName") String fileName) throws IOException {

        MedicalCertificate cert = certRepo.findById(Integer.parseInt(fileName)).orElse(null);

        /* first, get and initialize an engine */
        VelocityEngine ve = new VelocityEngine();

        /* next, get the Template */
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        ve.init();
        Template t = ve.getTemplate("templates/doctor/print_mc.html");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL d, YYYY");
        DateTimeFormatter certName = DateTimeFormatter.ofPattern("hhmmss");

        /* create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("name", cert.getPatientID().getFullName());
        context.put("address", cert.getPatientID().getAddress());
        context.put("date", cert.getDate());
        context.put("reason", cert.getReason());
        context.put("doctor", cert.getDoctorID().getFullName());

        context.put("genDateTime", LocalDateTime.now().format(formatter));
        /* now render the template into a StringWriter */
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        /* show the World */
        System.out.println(writer.toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos = generatePdf(writer.toString());

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + cert.getPatientID().getFullName().replace(" ", "_") + "_Certificate"
                        + LocalDateTime.now().format(certName)
                        + ".pdf");
        header.setContentLength(baos.toByteArray().length);

        return new HttpEntity<byte[]>(baos.toByteArray(), header);

    }

    public ByteArrayOutputStream generatePdf(String html) {

        String pdfFilePath = "";
        PdfWriter pdfWriter = null;

        // create a new document
        Document document = new Document();
        try {

            document = new Document();
            // document header attributes
            document.addAuthor("Kinns");
            document.addAuthor("Kinns123");
            document.addCreationDate();
            document.addProducer();
            document.addCreator("kinns123.github.io");
            document.addTitle("HTML to PDF using itext");
            document.setPageSize(PageSize.LETTER);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            // open document
            document.open();

            XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
            xmlWorkerHelper.getDefaultCssResolver(true);
            xmlWorkerHelper.parseXHtml(pdfWriter, document, new StringReader(
                    html));
            // close the document
            document.close();
            System.out.println("PDF generated successfully");

            return baos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @GetMapping("/msg")
    public String printMesssage() {
        return "this is the message";
    }

}