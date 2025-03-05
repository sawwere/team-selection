package ru.sfedu.teamselection.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.Track;

@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
@Service
public class ReportService {

    public final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] trackToExcelFile(Path path, Track track) throws IOException {
        XSSFWorkbook report = createExcelReport(track);
        File file = new File(String.valueOf(path.resolve(track.getName() + ".xlsx")));
        report.write(new FileOutputStream(file));
        report.close();
        return Files.readAllBytes(file.toPath());
    }

    public byte[] trackToExcelFile(Track track) throws IOException {
        return trackToExcelFile(Path.of("/"), track);
    }

    public XSSFWorkbook createExcelReport(Track track) {
        XSSFWorkbook report = createExcelFile();
        Sheet sheet = report.getSheet("Отчет");
        Row firstRow = sheet.createRow(1);

        firstRow.createCell(0).setCellValue(track.getName());
        firstRow.createCell(1).setCellValue(track.getAbout());
        firstRow.createCell(2).setCellValue(track.getStartDate().format(dtf));
        firstRow.createCell(3).setCellValue(track.getEndDate().format(dtf));
        firstRow.createCell(4).setCellValue(track.getType().toString());
        firstRow.createCell(5).setCellValue(String.valueOf(track.getMinConstraint()));
        firstRow.createCell(6).setCellValue(String.valueOf(track.getMaxConstraint()));
        firstRow.createCell(7).setCellValue(String.valueOf(track.getMaxSecondCourseConstraint()));

        int rowIndex = 2;
        for (Team team : track.getCurrentTeams()) {
            Row headerTeam = sheet.createRow(rowIndex++);
            createTeamHeader(headerTeam, report);
            Row rowTeam = sheet.createRow(rowIndex++);
            rowTeam.createCell(0).setCellValue(team.getName());
            rowTeam.createCell(1).setCellValue(team.getProjectDescription());
            rowTeam.createCell(2).setCellValue(team.getProjectType().getName());
            rowTeam.createCell(3).setCellValue(String.valueOf(team.getQuantityOfStudents()));
            rowTeam.createCell(4).setCellValue(String.valueOf(team.getIsFull()));
            rowTeam.createCell(5).setCellValue(
                    team.getTechnologies()
                            .stream()
                            .map(Technology::getName)
                            .collect(Collectors.joining(","))
            );

            Row headerStudent = sheet.createRow(rowIndex++);
            createStudentHeader(headerStudent, report);
            for (Student student : team.getStudents()) {
                Row rowStudent = sheet.createRow(rowIndex++);
                rowStudent.createCell(0).setCellValue(student.getUser().getFio());
                rowStudent.createCell(1).setCellValue(student.getUser().getEmail());
                rowStudent.createCell(2).setCellValue(String.valueOf(student.getCourse()));
                rowStudent.createCell(3).setCellValue(String.valueOf(student.getGroupNumber()));
                rowStudent.createCell(4).setCellValue(student.getAboutSelf());
                rowStudent.createCell(5).setCellValue(student.getTechnologies()
                        .stream()
                        .map(Technology::getName)
                        .collect(Collectors.joining(","))
                );
                rowStudent.createCell(6).setCellValue(String.valueOf(student.getIsCaptain()));
            }
        }
        return report;
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void createStudentHeader(Row row, XSSFWorkbook report) {
        CellStyle style = report.createCellStyle();
        Font newFont = report.createFont();
        newFont.setFontName("Calibri");
        newFont.setBold(true);
        newFont.setColor(IndexedColors.GREEN.getIndex());
        style.setFont(newFont);

        row.createCell(0).setCellValue("ФИО");
        row.getCell(0).setCellStyle(style);
        row.createCell(1).setCellValue("Email");
        row.getCell(1).setCellStyle(style);
        row.createCell(2).setCellValue("Курс");
        row.getCell(2).setCellStyle(style);
        row.createCell(3).setCellValue("Группа");
        row.getCell(3).setCellStyle(style);
        row.createCell(4).setCellValue("О себе");
        row.getCell(4).setCellStyle(style);
        row.createCell(5).setCellValue("Тэги");
        row.getCell(5).setCellStyle(style);
        row.createCell(6).setCellValue("Капитан");
        row.getCell(6).setCellStyle(style);
    }

    private void createTeamHeader(Row row, XSSFWorkbook report) {
        CellStyle style = report.createCellStyle();
        Font newFont = report.createFont();
        newFont.setFontName("Calibri");
        newFont.setBold(true);
        newFont.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(newFont);

        row.createCell(0).setCellValue("Имя команды");
        row.getCell(0).setCellStyle(style);
        row.createCell(1).setCellValue("Описание команды");
        row.getCell(1).setCellStyle(style);
        row.createCell(2).setCellValue("Тип проекта");
        row.getCell(2).setCellStyle(style);
        row.createCell(3).setCellValue("Количество студентов");
        row.getCell(3).setCellStyle(style);
        row.createCell(4).setCellValue("Заполненность команды");
        row.getCell(4).setCellStyle(style);
        row.createCell(5).setCellValue("Тэги");
        row.getCell(5).setCellStyle(style);
    }


    private XSSFWorkbook createExcelFile() {
        XSSFWorkbook report = new XSSFWorkbook();
        Sheet sheet = report.createSheet("Отчет");
        Row header = sheet.createRow(0);
        CellStyle style = report.createCellStyle();
        Font newFont = report.createFont();
        newFont.setFontName("Calibri");
        newFont.setBold(true);
        newFont.setColor(IndexedColors.RED.getIndex());
        style.setFont(newFont);

        header.createCell(0).setCellValue("Название трека");
        header.getCell(0).setCellStyle(style);
        header.createCell(1).setCellValue("Описание трека");
        header.getCell(1).setCellStyle(style);
        header.createCell(2).setCellValue("Дата начала");
        header.getCell(2).setCellStyle(style);
        header.createCell(3).setCellValue("Дата окончания");
        header.getCell(3).setCellStyle(style);
        header.createCell(4).setCellValue("Тип трека");
        header.getCell(4).setCellStyle(style);
        header.createCell(5).setCellValue("Минимум человек в команде");
        header.getCell(5).setCellStyle(style);
        header.createCell(6).setCellValue("Максимум человек в команде");
        header.getCell(6).setCellStyle(style);
        header.createCell(7).setCellValue("Максимум студентов 2 курса (для бакалавров)");
        header.getCell(7).setCellStyle(style);

        return report;
    }
}

