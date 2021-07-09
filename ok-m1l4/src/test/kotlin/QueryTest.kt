import homework.SqlSelectBuilder
import homework.query
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QueryTest {

    private fun doTest(expected: String, sql: SqlSelectBuilder.() -> Unit) {
        assertEquals(expected, query(sql).build())
    }

    @Test
    fun `when no columns then star used`() {
        doTest("select * from user") {
            from("user")
        }
    }

    @Test
    fun `when no condition is specified then correct query is built`() {
        doTest("select column1, column2 from table1") {
            select("column1", "column2")
            from ("table1")
        }
    }

    @Test
    fun `when a list of conditions is specified then it's respected`() {
        doTest("select * from table1 where (column3 = 4 and column4 is null)") {
            from ("table1")
            where {
                "column3" eq 4
                "column4" eq null
            }
        }
    }

    @Test
    fun `when 'or' conditions are specified then they are respected`() {
        doTest("select * from table1 where (column3 = 4 or column4 is null)") {
            from ("table1")
            where {
                or {
                    "column3" eq 4
                    "column4" eq null
                }
            }
        }
    }

    @Test
    fun `when either 'and' or 'or' conditions are specified then they are respected`() {
        doTest("select * from table1 where ((column3 = 4 or column4 is null) and column5 = 42)") {
            from ("table1")
            where {
                or {
                    "column3" eq 4
                    "column4" eq null
                }
                "column5" eq 42
            }
        }
    }

    @Test
    fun `test query` () {
        doTest("select id, name from user where ((department = 'finance' or department = 'IT') and age = '27')") {
            select("id", "name")
            from("user")
            where {
                or {
                    "department" eq "finance"
                    "department" eq "IT"
                }
                "age" eq "27"
            }
        }
    }
}