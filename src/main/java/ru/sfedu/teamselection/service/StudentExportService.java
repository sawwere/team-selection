package ru.sfedu.teamselection.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;

@Service
@RequiredArgsConstructor
public class StudentExportService {
    private final StudentService studentService;

    /**
     * Экспорт студентов по треку в CSV, сортировка по ФИО.
     * Добавлены столбцы "команда" и "контакты".
     */
    @Transactional(readOnly = true)
    public byte[] exportStudentsToCsvByTrack(Long trackId) {
        Sort sort = Sort.by("user.fio").ascending();
        List<Student> students = studentService.findAllByTrack(trackId, sort);

        String[] headers = {
                "id", "fio", "email", "course", "groupNumber",
                "hasTeam", "isCaptain", "track", "teamName", "contacts"
        };

        try (StringWriter sw = new StringWriter();
             CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(headers))) {
            for (Student st : students) {
                printer.printRecord(
                        st.getId(),
                        st.getUser().getFio(),
                        st.getUser().getEmail(),
                        st.getCourse(),
                        st.getGroupNumber(),
                        st.getHasTeam(),
                        st.getIsCaptain(),
                        st.getCurrentTrack().getName(),
                        Optional.ofNullable(st.getCurrentTeam()).map(t -> t.getName()).orElse(""),
                        Optional.ofNullable(st.getContacts()).orElse("")
                );
            }
            printer.flush();
            return sw.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при формировании CSV для trackId=" + trackId, e);
        }
    }

    /**
     * Экспорт студентов по треку в Excel, сортировка по ФИО.
     * Добавлены столбцы "Команда" и "Контакты".
     * Строки студентов в команде подсвечиваются зелёным.
     * Заголовки выделены цветом #330036, строки чередуются, автофильтр и заморозка.
     */
    @Transactional(readOnly = true)
    public byte[] exportStudentsToExcelByTrack(Long trackId) {
        Sort sort = Sort.by("user.fio").ascending();
        List<Student> students = studentService.findAllByTrack(trackId, sort);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Students_" + trackId);

            // Шрифты и стили
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            // Цвет шапки #330036
            byte[] hdr = new byte[]{(byte)0x33, (byte)0x00, (byte)0x36};
            headerStyle.setFillForegroundColor(new XSSFColor(hdr, new DefaultIndexedColorMap()));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            XSSFCellStyle greenStyle = workbook.createCellStyle();
            greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            greenStyle.setBorderTop(BorderStyle.THIN);
            greenStyle.setBorderBottom(BorderStyle.THIN);
            greenStyle.setBorderLeft(BorderStyle.THIN);
            greenStyle.setBorderRight(BorderStyle.THIN);
            greenStyle.setWrapText(true);
            greenStyle.setVerticalAlignment(VerticalAlignment.TOP);

            XSSFCellStyle whiteStyle = workbook.createCellStyle();
            whiteStyle.setBorderTop(BorderStyle.THIN);
            whiteStyle.setBorderBottom(BorderStyle.THIN);
            whiteStyle.setBorderLeft(BorderStyle.THIN);
            whiteStyle.setBorderRight(BorderStyle.THIN);
            whiteStyle.setWrapText(true);
            whiteStyle.setVerticalAlignment(VerticalAlignment.TOP);

            // Заголовки
            String[] headers = {
                    "ID", "ФИО", "Email", "Курс", "Группа",
                    "В команде", "Капитан", "Трек", "Команда", "Контакты"
            };
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            int rowIdx = 1;
            for (Student st : students) {
                XSSFRow row = sheet.createRow(rowIdx);
                boolean inTeam = Boolean.TRUE.equals(st.getHasTeam());
                XSSFCellStyle rowStyle = inTeam ? greenStyle : whiteStyle;

                Object[] data = {
                        st.getId(),
                        st.getUser().getFio(),
                        st.getUser().getEmail(),
                        st.getCourse(),
                        st.getGroupNumber(),
                        st.getHasTeam(),
                        st.getIsCaptain(),
                        st.getCurrentTrack().getName(),
                        Optional.ofNullable(st.getCurrentTeam()).map(t -> t.getName()).orElse(""),
                        Optional.ofNullable(st.getContacts()).orElse("")
                };

                for (int i = 0; i < data.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    if (data[i] instanceof Boolean) {
                        cell.setCellValue((Boolean) data[i]);
                    } else if (data[i] instanceof Number) {
                        cell.setCellValue(((Number) data[i]).doubleValue());
                    } else {
                        cell.setCellValue(data[i].toString());
                    }
                    cell.setCellStyle(rowStyle);
                }
                rowIdx++;
            }

            // Автофильтр и заморозка
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers.length - 1));
            sheet.createFreezePane(0, 1);

            // Авторазмер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при формировании Excel для trackId=" + trackId, e);
        }
    }
}
