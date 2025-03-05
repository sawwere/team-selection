package ru.sfedu.teamselection.service.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.service.ReportService;

public class TrackExcelExporterTest {
    @TempDir
    static Path tempDir = Path.of("test");


    @Test
    void trackToExcelFile_ShouldGenerateValidExcelFile() throws Exception {


        // Создаем тестовые данные
        Track track = createTestTrack();
        ReportService exporter = new ReportService();

        // Вызываем тестируемый метод
        byte[] excelBytes = exporter.trackToExcelFile(track);

        // Проверяем, что массив байтов не пустой
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        // Читаем Excel из байтов
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Отчет");
            assertNotNull(sheet, "Лист 'Отчет' должен существовать");

            // Проверка заголовков трека
            Row headerRow = sheet.getRow(0);
            assertHeaderCell(headerRow, 0, "Название трека", IndexedColors.RED, workbook);
            // Аналогично проверяем остальные заголовки...

            // Проверка данных трека
            Row trackRow = sheet.getRow(1);
            assertEquals(track.getName(), trackRow.getCell(0).getStringCellValue());
            assertEquals(track.getAbout(), trackRow.getCell(1).getStringCellValue());
            assertEquals(track.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), trackRow.getCell(2).getStringCellValue());
            // Проверяем остальные поля...

            // Проверка данных команды
            Row teamHeaderRow = sheet.getRow(2);
            assertHeaderCell(teamHeaderRow, 0, "Имя команды", IndexedColors.BLUE, workbook);
            // Проверяем остальные заголовки команды...

            Row teamDataRow = sheet.getRow(3);
            assertEquals("Team 1", teamDataRow.getCell(0).getStringCellValue());
            // Проверяем остальные данные команды...

            // Проверка данных студента
            Row studentHeaderRow = sheet.getRow(4);
            assertHeaderCell(studentHeaderRow, 0, "ФИО", IndexedColors.GREEN, workbook);
            // Проверяем остальные заголовки студентов...

            Row studentDataRow = sheet.getRow(5);
            assertEquals("Иван Викторович", studentDataRow.getCell(0).getStringCellValue());
            // Проверяем остальные данные студента...
        }
    }

    private Track createTestTrack() {
        Track track = new Track();
        track.setName("Test Track");
        track.setAbout("Test Description");
        track.setStartDate(LocalDate.of(2024, 9, 1));
        track.setEndDate(LocalDate.of(2025, 5, 31));
        track.setType(TrackType.bachelor);
        track.setMinConstraint(3);
        track.setMaxConstraint(5);
        track.setMaxSecondCourseConstraint(2);

        Team team = new Team();
        team.setName("Team 1");
        team.setProjectDescription("Project Desc");
        team.setProjectType(new ProjectType(1L, "Web"));
        team.setQuantityOfStudents(1);
        team.setIsFull(false);
        team.setTechnologies(List.of(new Technology(1L, "Java"), new Technology(2L, "Spring")));


        Student student = Student.builder()
                .id(1L)
                .user(User.builder()
                        .id(662L)
                        .fio("Иван Викторович")
                        .email("test@example.com")
                        .isRemindEnabled(true)
                        .build())
                .course(2)
                .groupNumber(12)
                .aboutSelf("about self")
                .contacts("contacts")
                .technologies(List.of(new Technology(53L, "Python")))
                .isCaptain(true)
                .currentTeam(team)
                .build();

        team.setStudents(List.of(student));
        track.setCurrentTeams(List.of(team));

        return track;
    }

    private void assertHeaderCell(Row row, int cellIndex, String expectedValue, IndexedColors expectedColor, Workbook workbook) {
        Cell cell = row.getCell(cellIndex);
        assertNotNull(cell, "Ячейка " + cellIndex + " не должна быть null");
        assertEquals(expectedValue, cell.getStringCellValue());

        CellStyle style = cell.getCellStyle();
        Font font = workbook.getFontAt(style.getFontIndex());
        assertTrue(font.getBold(), "Шрифт должен быть жирным");
        assertEquals(expectedColor.getIndex(), font.getColor(), "Неверный цвет шрифта");
    }
}
