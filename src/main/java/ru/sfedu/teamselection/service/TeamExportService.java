package ru.sfedu.teamselection.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;

@Service
@RequiredArgsConstructor
public class TeamExportService {
    private final TeamService teamService;


    /**
     * Экспорт команд по треку в Excel, сортировка по названию команды.
     * Столбец "Капитан" — ФИО капитана.
     * Полные команды подсвечиваются светло-зелёным.
     */
    @Transactional(readOnly = true)
    public byte[] exportTeamsToCsvByTrack(Long trackId) {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending());
        List<Team> teams = teamService.search(null, trackId, null, null, null, all).getContent();

        String[] headers = {
                "id",
                "name",
                "projectDescription",
                "projectType",
                "quantityOfStudents",
                "captainFio",
                "isFull",
                "track",
                "technologies"
        };

        try (StringWriter sw = new StringWriter();
             CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(headers))) {

            for (Team team : teams) {
                String captainFio = team.getStudents().stream()
                        .filter(s -> s.getId().equals(team.getCaptainId()))
                        .map(Student::getUser)
                        .map(u -> u.getFio())
                        .findFirst()
                        .orElse("");

                String techList = team.getTechnologies().stream()
                        .map(t -> t.getName())
                        .collect(Collectors.joining("; "));

                printer.printRecord(
                        team.getId(),
                        team.getName(),
                        team.getProjectDescription(),
                        Optional.ofNullable(team.getProjectType()).map(pt -> pt.getName()).orElse(""),
                        team.getQuantityOfStudents(),
                        captainFio,
                        team.getIsFull(),
                        Optional.ofNullable(team.getCurrentTrack()).map(tr -> tr.getName()).orElse(""),
                        techList
                );
            }

            printer.flush();
            return sw.toString().getBytes(StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при формировании CSV для trackId=" + trackId, e);
        }
    }


    /**
     * Экспорт команд по треку в Excel, сортировка по названию команды.
     * Столбец "Капитан" — ФИО капитана.
     * Заголовки выделены цветом #330036, строки с чередованием цвета, автофильтр и заморозка.
     */
    @Transactional(readOnly = true)
    public byte[] exportTeamsToExcelByTrack(Long trackId) {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending());
        List<Team> teams = teamService.search(null, trackId, null, null, null, all).getContent();

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Teams_" + trackId);

            // Шрифты и стили
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            // Custom header background color #330036
            byte[] hexColor = new byte[]{(byte)0x33, (byte)0x00, (byte)0x36};
            XSSFColor headerColor = new XSSFColor(hexColor, new DefaultIndexedColorMap());
            headerStyle.setFillForegroundColor(headerColor);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            XSSFCellStyle evenStyle = workbook.createCellStyle();
            byte[] grey = new byte[]{(byte)0xF2, (byte)0xF2, (byte)0xF2};
            evenStyle.setFillForegroundColor(new XSSFColor(grey, new DefaultIndexedColorMap()));
            evenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            evenStyle.setBorderTop(BorderStyle.THIN);
            evenStyle.setBorderBottom(BorderStyle.THIN);
            evenStyle.setBorderLeft(BorderStyle.THIN);
            evenStyle.setBorderRight(BorderStyle.THIN);
            evenStyle.setVerticalAlignment(VerticalAlignment.TOP);
            evenStyle.setWrapText(true);

            XSSFCellStyle oddStyle = workbook.createCellStyle();
            oddStyle.setBorderTop(BorderStyle.THIN);
            oddStyle.setBorderBottom(BorderStyle.THIN);
            oddStyle.setBorderLeft(BorderStyle.THIN);
            oddStyle.setBorderRight(BorderStyle.THIN);
            oddStyle.setVerticalAlignment(VerticalAlignment.TOP);
            oddStyle.setWrapText(true);

            // Заголовок
            String[] headers = {
                    "ID",
                    "Название команды",
                    "Описание проекта",
                    "Тип проекта",
                    "Кол-во студентов",
                    "Капитан",
                    "Полная",
                    "Трек",
                    "Технологии"
            };
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            int rowIdx = 1;
            for (Team team : teams) {
                XSSFRow row = sheet.createRow(rowIdx);
                boolean isEven = (rowIdx % 2 == 0);
                XSSFCellStyle rowStyle = isEven ? evenStyle : oddStyle;

                String captainFio = team.getStudents().stream()
                        .filter(s -> s.getId().equals(team.getCaptainId()))
                        .map(Student::getUser)
                        .map(u -> u.getFio())
                        .findFirst()
                        .orElse("");

                String techList = team.getTechnologies().stream()
                        .map(t -> t.getName())
                        .collect(Collectors.joining("; "));

                String[] data = {
                        team.getId().toString(),
                        team.getName(),
                        Optional.ofNullable(team.getProjectDescription()).orElse(""),
                        Optional.ofNullable(team.getProjectType()).map(pt -> pt.getName()).orElse(""),
                        team.getQuantityOfStudents().toString(),
                        captainFio,
                        team.getIsFull().toString(),
                        Optional.ofNullable(team.getCurrentTrack()).map(tr -> tr.getName()).orElse(""),
                        techList
                };

                for (int i = 0; i < data.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(data[i]);
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
