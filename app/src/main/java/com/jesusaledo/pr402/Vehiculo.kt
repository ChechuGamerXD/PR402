package com.jesusaledo.pr402

abstract class Vehiculo {
    var ruedas = 0
    var motor = 0
    var asientos = 0
    var color = ""
    var modelo = ""

    override fun toString(): String {
        return "Tipo: ${javaClass.simpleName}, Modelo: $modelo, Color: $color, Ruedas: $ruedas, Caballos de potencia: $motor, Asientos: $asientos"
    }
}

class Coche : Vehiculo()

class Moto : Vehiculo()

class Patinete : Vehiculo()

class Furgoneta : Vehiculo() {
    var pesoMaximo = 0

    override fun toString(): String {
        return super.toString() + ", Peso Máximo: $pesoMaximo"
    }
}

class Trailer : Vehiculo() {
    var pesoMaximo = 0

    override fun toString(): String {
        return super.toString() + ", Peso Máximo: $pesoMaximo"
    }
}

class Comodin: Vehiculo(){
    // Esto es muy feo pero el null dejó de funcionar por lo que sea
}

