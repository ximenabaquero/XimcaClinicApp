@Entity(tableName = "pacientes")
data class Paciente(
    primaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String,
    val peso: Double, //kg
    val estatura: Double,
    val imc: Double,
    val antecedentes: String = "",
    val estado: String = "EN_ESPERA"
)
