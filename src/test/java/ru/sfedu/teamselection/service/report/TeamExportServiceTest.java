package ru.sfedu.teamselection.service.report;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.service.TeamExportService;
import ru.sfedu.teamselection.service.TeamService;

class TeamExportServiceTest {

    @Mock
    private TeamService teamService;

    private TeamExportService teamExportService;

    private List<Team> testTeams;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        teamExportService = new TeamExportService(teamService);

        // Подготовка тестовых данных
        User captainUser = new User();
        captainUser.setFio("Иванов Иван Иванович");

        Student captain = new Student();
        captain.setId(1L);
        captain.setUser(captainUser);

        Track track = new Track();
        track.setName("Java Developer");

        Technology java = new Technology();
        java.setName("Java");
        Technology spring = new Technology();
        spring.setName("Spring");

        Team team1 = Team.builder()
                .id(1L)
                .name("Dream Team")
                .projectDescription("Awesome project")
                .captainId(1L)
                .students(List.of(captain))
                .technologies(List.of(java, spring))
                .currentTrack(track)
                .quantityOfStudents(1)
                .isFull(false)
                .build();

        testTeams = List.of(team1);
    }

    @Test
    void exportTeamsToCsvByTrack_ShouldGenerateValidCsv() throws Exception {
        // Arrange
        Long trackId = 1L;
        when(teamService.search(any(), eq(trackId), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(testTeams));

        // Act
        byte[] result = teamExportService.exportTeamsToCsvByTrack(trackId);
        String csv = new String(result);

        // Assert
        assertTrue(csv.startsWith("id,name,projectDescription,projectType,quantityOfStudents,captainFio,isFull,track,technologies"));
        assertTrue(csv.contains("Dream Team"));
        assertTrue(csv.contains("Java; Spring"));
    }

    @Test
    void exportTeamsToExcelByTrack_ShouldCreateValidWorkbookStructure() throws Exception {
        // Arrange
        Long trackId = 1L;
        when(teamService.search(any(), eq(trackId), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(testTeams));

        // Act
        byte[] excelData = teamExportService.exportTeamsToExcelByTrack(trackId);
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData));
        Sheet sheet = workbook.getSheetAt(0);

        // Assert
        assertEquals("Teams_1", sheet.getSheetName());
        assertEquals(2, sheet.getPhysicalNumberOfRows()); // Header + 1 teams

        // Проверка заголовков
        Row headerRow = sheet.getRow(0);
        assertEquals("ID", headerRow.getCell(0).getStringCellValue());
        assertEquals("Технологии", headerRow.getCell(8).getStringCellValue());

        // Проверка стилей заголовка
        CellStyle headerStyle = headerRow.getCell(0).getCellStyle();
        assertEquals(FillPatternType.SOLID_FOREGROUND, headerStyle.getFillPattern());
        assertEquals(HorizontalAlignment.CENTER, headerStyle.getAlignment());

        // Проверка данных
        Row firstDataRow = sheet.getRow(1);
        assertEquals("Dream Team", firstDataRow.getCell(1).getStringCellValue());
        assertEquals("Java; Spring", firstDataRow.getCell(8).getStringCellValue());
    }

    @Test
    void exportTeamsToExcelByTrack_ShouldHandleMissingData() throws Exception {
        // Arrange
        Long trackId = 1L;
        testTeams.get(0).setCurrentTrack(null);
        testTeams.get(0).setTechnologies(List.of());
        when(teamService.search(any(), eq(trackId), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(testTeams));

        // Act
        byte[] excelData = teamExportService.exportTeamsToExcelByTrack(trackId);
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData));
        Row row = workbook.getSheetAt(0).getRow(1);

        // Assert
        assertEquals("", row.getCell(7).getStringCellValue()); // Трек
        assertEquals("", row.getCell(8).getStringCellValue()); // Технологии
    }

    @Test
    void exportMethods_ShouldThrowExceptionOnServiceError() {
        // Arrange
        Long trackId = 1L;
        when(teamService.search(any(), eq(trackId), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> teamExportService.exportTeamsToCsvByTrack(trackId));
        assertThrows(RuntimeException.class,
                () -> teamExportService.exportTeamsToExcelByTrack(trackId));
    }
}