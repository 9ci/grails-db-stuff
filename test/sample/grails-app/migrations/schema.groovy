
databaseChangeLog = {
    changeSet(author: "basejump (generated)", id: "1353445291248-1") {
        createTable(tableName: "Test") {

            column(name: "Name", type: "VARCHAR(4)")

            column(name: "Description", type: "VARCHAR(50)")

            column(name: "Age", type: "BIGINT")

        }
    }
}
