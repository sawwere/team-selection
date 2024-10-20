package ru.sfedu.teamselection.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.opendevl.JFlat
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import ru.sfedu.teamselection.domain.Student
import ru.sfedu.teamselection.domain.Team
import ru.sfedu.teamselection.domain.Track
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Service
class ReportService {

    private val sdf = SimpleDateFormat("dd.MM.yyyy")


    @Bean
    fun test() {
        trackToExcelFile(
            Track(
                name = "first track",
                currentTeams = mutableListOf(
                    Team(
                        name = "test",
                        students = mutableListOf(Student(fio = "testfio1"), Student("testfio2"))
                    ),
                    Team(
                        name = "test2",
                        students = mutableListOf(Student(fio = "testfio3"), Student("testfio4"))
                    )
                ),
                startDate = Date(),
                endDate = Date(),
                type = "bachelor",
                maxThirdCourseConstraint = 1
            )
        )
    }

    fun trackToExcelFile(track: Track): ByteArray {
        val report = createExcelReport(track)
        val file = File("${track.name}.xlsx")
        report.write(file.outputStream())
        report.close()
        return file.readBytes()
    }

    fun createExcelReport(track: Track): XSSFWorkbook {
        val report = createExcelFile()
        val sheet = report.getSheet("Отчет")
        val firstRow = sheet.createRow(1)
        firstRow.apply {
            createCell(0, CellType.STRING).apply {
                setCellValue(track.name)
            }
            createCell(1, CellType.STRING).apply {
                setCellValue(track.about)
            }
            createCell(2, CellType.STRING).apply {
                setCellValue(sdf.format(track.startDate))
            }
            createCell(3, CellType.STRING).apply {
                setCellValue(sdf.format(track.endDate))
            }
            createCell(4, CellType.STRING).apply {
                setCellValue(track.type)
            }
            createCell(5, CellType.STRING).apply {
                setCellValue(track.minConstraint.toString())
            }
            createCell(6, CellType.STRING).apply {
                setCellValue(track.maxConstraint.toString())
            }
            createCell(7, CellType.STRING).apply {
                setCellValue(track.maxThirdCourseConstraint.toString())
            }
        }
        var rowIndex = 2
        track.currentTeams?.forEach { team ->
            val headerTeam = sheet.createRow(rowIndex++)
            createTeamHeader(headerTeam, report)
            val rowTeam = sheet.createRow(rowIndex++)
            rowTeam.apply {
                createCell(0, CellType.STRING).apply {
                    setCellValue(team.name)
                }
                createCell(1, CellType.STRING).apply {
                    setCellValue(team.about)
                }
                createCell(2, CellType.STRING).apply {
                    setCellValue(team.projectType)
                }
                createCell(3, CellType.STRING).apply {
                    setCellValue(team.quantityOfStudents.toString())
                }
                createCell(4, CellType.STRING).apply {
                    setCellValue(team.fullFlag.toString())
                }
                createCell(5, CellType.STRING).apply {
                    setCellValue(team.tags)
                }
            }
            val headerStudent = sheet.createRow(rowIndex++)
            createStudentHeader(headerStudent, report)
            team.students?.forEach { student ->
                val rowStudent = sheet.createRow(rowIndex++)
                rowStudent.apply {
                    createCell(0, CellType.STRING).apply {
                        setCellValue(student.fio)
                    }
                    createCell(1, CellType.STRING).apply {
                        setCellValue(student.email)
                    }
                    createCell(2, CellType.STRING).apply {
                        setCellValue(student.course.toString())
                    }
                    createCell(3, CellType.STRING).apply {
                        setCellValue(student.groupNumber.toString())
                    }
                    createCell(4, CellType.STRING).apply {
                        setCellValue(student.aboutSelf)
                    }
                    createCell(5, CellType.STRING).apply {
                        setCellValue(student.tags)
                    }
                    createCell(0, CellType.STRING).apply {
                        setCellValue(student.captain.toString())
                    }
                }
            }
        }
        return report
    }

    private fun createStudentHeader(row: XSSFRow, report: XSSFWorkbook) {
        val style = report.createCellStyle().apply {
            val newFont = report.createFont().apply {
                fontName = "Calibri"
                bold = true
                color = HSSFColorPredefined.GREEN.index
            }
            setFont(newFont)
        }
        row.apply {
            createCell(0, CellType.STRING).apply {
                setCellValue("ФИО")
                cellStyle = style
            }
            createCell(1, CellType.STRING).apply {
                setCellValue("Email")
                cellStyle = style
            }
            createCell(2, CellType.STRING).apply {
                setCellValue("Курс")
                cellStyle = style
            }
            createCell(3, CellType.STRING).apply {
                setCellValue("Группа")
                cellStyle = style
            }
            createCell(4, CellType.STRING).apply {
                setCellValue("О себе")
                cellStyle = style
            }
            createCell(5, CellType.STRING).apply {
                setCellValue("Тэги")
                cellStyle = style
            }
            createCell(6, CellType.STRING).apply {
                setCellValue("Капитан")
                cellStyle = style
            }
        }

    }

    private fun createTeamHeader(row: XSSFRow, report: XSSFWorkbook) {
        val style = report.createCellStyle().apply {
            val newFont = report.createFont().apply {
                fontName = "Calibri"
                bold = true
                color = HSSFColorPredefined.BLUE.index
            }
            setFont(newFont)
        }
        row.apply {
            createCell(0, CellType.STRING).apply {
                setCellValue("Имя команды")
                cellStyle = style
            }
            createCell(1, CellType.STRING).apply {
                setCellValue("Описание команды")
                cellStyle = style
            }
            createCell(2, CellType.STRING).apply {
                setCellValue("Тип проекта")
                cellStyle = style
            }
            createCell(3, CellType.STRING).apply {
                setCellValue("Количество студентов")
                cellStyle = style
            }
            createCell(4, CellType.STRING).apply {
                setCellValue("Заполненность команды")
                cellStyle = style
            }
            createCell(5, CellType.STRING).apply {
                setCellValue("Тэги")
                cellStyle = style
            }
        }
    }

    private fun createExcelFile(): XSSFWorkbook {
        val report = XSSFWorkbook()
        val sheet = report.createSheet("Отчет")
        val header = sheet.createRow(0)
        val style = report.createCellStyle().apply {
            val newFont = report.createFont().apply {
                fontName = "Calibri"
                bold = true
                color = HSSFColorPredefined.RED.index
            }
            setFont(newFont)
        }

        header.createCell(0, CellType.STRING).apply {
            setCellValue("Название трека")
            cellStyle = style
        }
        header.createCell(1, CellType.STRING).apply {
            setCellValue("Описание трека")
            cellStyle = style
        }
        header.createCell(2, CellType.STRING).apply {
            setCellValue("Дата начала")
            cellStyle = style
        }
        header.createCell(3, CellType.STRING).apply {
            setCellValue("Дата окончания")
            cellStyle = style
        }
        header.createCell(4, CellType.STRING).apply {
            setCellValue("Тип трека")
            cellStyle = style
        }
        header.createCell(5, CellType.STRING).apply {
            setCellValue("Минимум человек в команде")
            cellStyle = style
        }
        header.createCell(6, CellType.STRING).apply {
            setCellValue("Максимум человек в команде")
            cellStyle = style
        }
        header.createCell(7, CellType.STRING).apply {
            setCellValue("Максимум студентов 3 курса (для бакалавров)")
            cellStyle = style
        }
        return report
    }
}