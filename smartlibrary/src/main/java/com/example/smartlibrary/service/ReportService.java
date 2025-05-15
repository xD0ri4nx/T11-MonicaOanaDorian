package com.example.smartlibrary.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final LoanService loanService;
    private final BookService bookService;
    private final UserService userService;
    private final ReservationService reservationService;

    public ReportService(LoanService loanService, BookService bookService,
                         UserService userService, ReservationService reservationService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    public byte[] generateMonthlyActivityReport(LocalDate month) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Library Activity Report - " +
                    month.format(DateTimeFormatter.ofPattern("MMMM yyyy")), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 1. Loans statistics
            addSectionTitle(document, "Loans Statistics");
            Map<String, Long> loansStats = loanService.getMonthlyLoanStats(month);
            addKeyValueTable(document, loansStats);

            // 2. Most borrowed books
            addSectionTitle(document, "Top 5 Most Borrowed Books");
            List<Map<String, Object>> popularBooks = bookService.getMostBorrowedBooks(month, 5);
            addBooksTable(document, popularBooks);

            // 3. User activity
            addSectionTitle(document, "User Activity");
            Map<String, Long> userStats = userService.getUserActivityStats(month);
            addKeyValueTable(document, userStats);

            // 4. Overdue returns
            addSectionTitle(document, "Overdue Returns");
            List<Map<String, Object>> overdueLoans = loanService.getOverdueLoansForMonth(month);
            addOverdueLoansTable(document, overdueLoans);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    // Helper methods for PDF generation
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph section = new Paragraph(title, sectionFont);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }

    private void addKeyValueTable(Document document, Map<String, Long> data) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Header
        addTableHeaderCell(table, "Metric");
        addTableHeaderCell(table, "Value");

        // Data
        data.forEach((key, value) -> {
            table.addCell(createCell(key));
            table.addCell(createCell(value.toString()));
        });

        document.add(table);
    }

    private void addBooksTable(Document document, List<Map<String, Object>> books) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Header
        addTableHeaderCell(table, "Title");
        addTableHeaderCell(table, "Author");
        addTableHeaderCell(table, "Borrow Count");

        // Data
        books.forEach(book -> {
            table.addCell(createCell(book.get("title").toString()));
            table.addCell(createCell(book.get("author").toString()));
            table.addCell(createCell(book.get("borrowCount").toString()));
        });

        document.add(table);
    }

    private void addOverdueLoansTable(Document document, List<Map<String, Object>> loans) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Header
        addTableHeaderCell(table, "Book");
        addTableHeaderCell(table, "User");
        addTableHeaderCell(table, "Due Date");
        addTableHeaderCell(table, "Days Overdue");

        // Data
        loans.forEach(loan -> {
            table.addCell(createCell(loan.get("bookTitle").toString()));
            table.addCell(createCell(loan.get("userName").toString()));
            table.addCell(createCell(loan.get("dueDate").toString()));
            table.addCell(createCell(loan.get("daysOverdue").toString()));
        });

        document.add(table);
    }

    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5);
        return cell;
    }
}