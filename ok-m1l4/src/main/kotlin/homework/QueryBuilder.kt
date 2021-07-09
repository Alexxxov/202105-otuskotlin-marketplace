package homework

class SqlSelectBuilder {

    private lateinit var table: String
    private val columns = mutableListOf<String>()
    private var condition: Condition? = null


    fun select(vararg columns: String) {
        if (columns.isEmpty()) {
            throw IllegalArgumentException("At least one column should be defined")
        }
        if (this.columns.isNotEmpty()) {
            throw IllegalStateException("Detected attempt to redefine columns to fetch. "
                    + "Current columns are: ${this.columns}, "
                    + "new columns: $columns")
        }
        this.columns.addAll(columns)
    }

    fun from(table: String) {
        this.table = table
    }

    fun where(block: Condition.() -> Unit) {
        condition = And().apply(block)
    }

    fun build(): String {
        if (!::table.isInitialized) {
            throw IllegalStateException("Failed to build an sql select - target table is undefined")
        }
        return toString()
    }

    override fun toString(): String {
        val columnsToFetch =
            if (columns.isEmpty()) {
                "*"
            } else {
                columns.joinToString(", ")
            }
        val conditionString =
            if(condition == null) {
                ""
            } else {
                " where $condition"
            }
        return "select $columnsToFetch from $table$conditionString"
    }
}

abstract class Condition {
    infix fun String.eq(value: Any?) {
        addCondition(Eq(this, value))
    }
    fun and(block: Condition.() -> Unit) {
        addCondition(And().apply(block))
    }

    fun or(block: Condition.() -> Unit) {
        addCondition(Or().apply(block))
    }
    protected abstract fun addCondition(condition: Condition)
}

open class CompositeCondition(private val sqlOperator: String): Condition() {
    private val conditions = mutableListOf<Condition>()

    override fun addCondition(condition: Condition) {
        conditions += condition
    }

    override fun toString(): String {
        return if (conditions.size == 1) {
            conditions.first().toString()
        } else {
            conditions.joinToString(prefix = "(", postfix = ")", separator = " $sqlOperator ")
        }
    }
}

class And : CompositeCondition("and")

class Or: CompositeCondition("or")

class Eq(private val column: String, private val value: Any?) : Condition() {

    init {
        if (value != null && value !is Number && value !is String) {
            throw IllegalArgumentException(
                "Only <null>, numbers and strings values can be used in the 'where' clause")
        }
    }

    override fun addCondition(condition: Condition) {
        throw IllegalStateException("Can't add a nested condition to the sql 'eq'")
    }

    override fun toString(): String {
        return when (value) {
            null -> "$column is null"
            is String -> "$column = '$value'"
            else -> "$column = $value"
        }
    }
}

fun query(block: SqlSelectBuilder.() -> Unit) = SqlSelectBuilder().apply(block)




