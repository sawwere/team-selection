package ru.sfedu.teamselection.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
@Service
public class ReportService {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    //TODO
//    @Bean
//    public byte[] test() throws IOException {
//        Team team1 = Team.builder()
//                    .name("test")
//                    .students(List.of(
//                                Student.builder().fio("testfio1").build(),
//                                Student.builder().fio("testfio2").build()
//                            ))
//                    .build();
//
//        Team team2 = Team.builder()
//                .name("test2")
//                .students(List.of(
//                        Student.builder().fio("testfio3").build(),
//                        Student.builder().fio("testfio4").build()
//                ))
//                .build();
//
//
//
//        List<Team> teams = List.of(team1, team2);
//        return trackToExcelFile(
//                Track.builder().name("first track").currentTeams(teams
//                ).startDate(LocalDate.of(2024, 10, 22)).endDate(LocalDate.of(2025, 10, 22)).type("bachelor").maxThirdCourseConstraint(1).build()
//                        );
//    }

    public byte[] trackToExcelFile(Track track) throws IOException {
        XSSFWorkbook report = createExcelReport(track);
        File file = new File(track.getName() + ".xlsx");
        report.write(new FileOutputStream(file));
        report.close();
        return Files.readAllBytes(file.toPath());
    }

    public XSSFWorkbook createExcelReport(Track track) {
        XSSFWorkbook report = createExcelFile();
        Sheet sheet = report.getSheet("Отчет");
        Row firstRow = sheet.createRow(1);

        firstRow.createCell(0).setCellValue(track.getName());
        firstRow.createCell(1).setCellValue(track.getAbout());
        firstRow.createCell(2).setCellValue(sdf.format(track.getStartDate()));
        firstRow.createCell(3).setCellValue(sdf.format(track.getEndDate()));
        //firstRow.createCell(4).setCellValue(track.getType());
        firstRow.createCell(5).setCellValue(String.valueOf(track.getMinConstraint()));
        firstRow.createCell(6).setCellValue(String.valueOf(track.getMaxConstraint()));
        //TODO Убрал пока менял классы сущщностей
        //firstRow.createCell(7).setCellValue(String.valueOf(track.getMaxThirdCourseConstraint()));

        int rowIndex = 2;
        for (Team team : track.getCurrentTeams()) {
            Row headerTeam = sheet.createRow(rowIndex++);
            createTeamHeader(headerTeam, report);
            Row rowTeam = sheet.createRow(rowIndex++);
            rowTeam.createCell(0).setCellValue(team.getName());
            //TODO Убрал пока менял классы сущщностей
            //rowTeam.createCell(1).setCellValue(team.getAbout());
            //rowTeam.createCell(2).setCellValue(team.getProjectType());
            rowTeam.createCell(3).setCellValue(String.valueOf(team.getQuantityOfStudents()));
            rowTeam.createCell(4).setCellValue(String.valueOf(team.getIsFull()));
            //TODO Убрал пока менял классы сущщностей
            //rowTeam.createCell(5).setCellValue(team.getTags());

            Row headerStudent = sheet.createRow(rowIndex++);
            createStudentHeader(headerStudent, report);
            for (Student student : team.getStudents()) {
                Row rowStudent = sheet.createRow(rowIndex++);
                //TODO Убрал пока менял классы сущщностей
                //rowStudent.createCell(0).setCellValue(student.getFio());
                //TODO Убрал пока менял классы сущщностей
                //rowStudent.createCell(1).setCellValue(student.getEmail());
                rowStudent.createCell(2).setCellValue(String.valueOf(student.getCourse()));
                rowStudent.createCell(3).setCellValue(String.valueOf(student.getGroupNumber()));
                rowStudent.createCell(4).setCellValue(student.getAboutSelf());
                //TODO Убрал пока менял классы сущщностей
                //rowStudent.createCell(5).setCellValue(student.getTags());
                //TODO Убрал пока менял классы сущщностей
                //rowStudent.createCell(6).setCellValue(String.valueOf(student.getCaptain()));
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
        header.createCell(7).setCellValue("Максимум студентов 3 курса (для бакалавров)");
        header.getCell(7).setCellStyle(style);

        return report;
    }
}

