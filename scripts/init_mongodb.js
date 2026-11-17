// Seleccionar la base de datos del proyecto
db = db.getSiblingDB('AD_refugio');

// Limpiar la base de datos si ya existía para evitar duplicados al re-ejecutar
db.dropDatabase();

// 1. COLECCIÓN 'refugio': Información general del centro
db.refugio.insertOne({
  "_id": "REF01",
  "nombre": "Refugio Huellas Vitoria",
  "cif": "G01234567",
  "fechaApertura": "2020-05-15",
  "direccion": "Calle Portal de Foronda 45, Vitoria-Gasteiz",
  "contacto": "945001122 - info@huellasvitoria.es"
});

// 2. COLECCIÓN 'animales': Cubre los 4 estados (disponible, reservado, adoptado, en tratamiento)
db.animales.insertMany([
  {
    "idAnimal": "A001",
    "nombre": "Toby",
    "especie": "Perro",
    "raza": "Mestizo",
    "edad": 2,
    "sexo": "Macho",
    "descripcion": "Muy sociable, acostumbrado a convivir con niños",
    "fechaIngreso": "2026-09-10",
    "estado": "adoptado",
    "activo": true,
    "historialMedico": [
      {
        "fecha": "2026-09-12",
        "diagnostico": "Revisión inicial y desparasitación",
        "tratamiento": "Vacuna polivalente y microchip"
      },
      {
        "fecha": "2026-10-01",
        "diagnostico": "Control previo a adopción",
        "tratamiento": "Alta veterinaria"
      }
    ]
  },
  {
    "idAnimal": "A002",
    "nombre": "Luna",
    "especie": "Gato",
    "raza": "Común Europeo",
    "edad": 1,
    "sexo": "Hembra",
    "descripcion": "Tranquila y cariñosa, ideal para piso",
    "fechaIngreso": "2026-10-05",
    "estado": "disponible",
    "activo": true,
    "historialMedico": [
      {
        "fecha": "2026-10-06",
        "diagnostico": "Chequeo general y test leucemia negativo",
        "tratamiento": "Vacuna trivalente felina"
      }
    ]
  },
  {
    "idAnimal": "A003",
    "nombre": "Rocky",
    "especie": "Perro",
    "raza": "Bóxer",
    "edad": 4,
    "sexo": "Macho",
    "descripcion": "Energético, necesita paseos largos diarios",
    "fechaIngreso": "2026-10-20",
    "estado": "reservado",
    "activo": true,
    "historialMedico": [
      {
        "fecha": "2026-10-21",
        "diagnostico": "Otitis leve en oído derecho",
        "tratamiento": "Gotas óticas durante 7 días"
      }
    ]
  },
  {
    "idAnimal": "A004",
    "nombre": "Nala",
    "especie": "Gato",
    "raza": "Siamés",
    "edad": 3,
    "sexo": "Hembra",
    "descripcion": "Rescatada con fractura leve en pata trasera",
    "fechaIngreso": "2026-11-01",
    "estado": "en tratamiento",
    "activo": true,
    "historialMedico": [
      {
        "fecha": "2026-11-02",
        "diagnostico": "Fisura en fémur posterior",
        "tratamiento": "Inmovilización y antiinflamatorios"
      }
    ]
  }
]);

// 3. COLECCIÓN 'adoptantes': Incluye array de preferencias de adopción
db.adoptantes.insertMany([
  {
    "dni": "12345678Z",
    "nombre": "Laura",
    "apellidos": "Gómez Pérez",
    "telefono": "611223344",
    "direccion": "Avenida Gasteiz 12, Vitoria-Gasteiz",
    "email": "laura.gomez@email.com",
    "fechaRegistro": "2026-09-15",
    "activo": true,
    "preferencias": ["Perro", "Tamaño mediano", "Sociable"]
  },
  {
    "dni": "87654321X",
    "nombre": "Mikel",
    "apellidos": "Etxebarria Agirre",
    "telefono": "622334455",
    "direccion": "Calle Dato 28, Vitoria-Gasteiz",
    "email": "mikel.etxe@email.com",
    "fechaRegistro": "2026-10-25",
    "activo": true,
    "preferencias": ["Perro", "Activo"]
  },
  {
    "dni": "44556677M",
    "nombre": "Ane",
    "apellidos": "Ruiz de Azúa",
    "telefono": "633445566",
    "direccion": "Calle Prado 8, Vitoria-Gasteiz",
    "email": "ane.ruiz@email.com",
    "fechaRegistro": "2026-11-05",
    "activo": true,
    "preferencias": ["Gato", "Tranquilo"]
  }
]);

// 4. COLECCIÓN 'adopciones': Cubre estados 'aprobada', 'pendiente' y 'rechazada'
db.adopciones.insertMany([
  {
    "idAdopcion": "AD001",
    "idAnimal": "A001",
    "dniAdoptante": "12345678Z",
    "fechaSolicitud": "2026-09-20",
    "fechaResolucion": "2026-10-02",
    "estado": "aprobada",
    "observaciones": "Entrevista domiciliaria favorable",
    "activo": true,
    "seguimiento": [
      {
        "fecha": "2026-10-18",
        "notas": "Primera llamada: Toby se ha adaptado perfectamente al hogar"
      },
      {
        "fecha": "2026-11-02",
        "notas": "Visita presencial al mes: estado de salud óptimo"
      }
    ]
  },
  {
    "idAdopcion": "AD002",
    "idAnimal": "A003",
    "dniAdoptante": "87654321X",
    "fechaSolicitud": "2026-11-10",
    "fechaResolucion": null,
    "estado": "pendiente",
    "observaciones": "Pendiente de segunda visita con la familia",
    "activo": true,
    "seguimiento": []
  },
  {
    "idAdopcion": "AD003",
    "idAnimal": "A002",
    "dniAdoptante": "44556677M",
    "fechaSolicitud": "2026-10-15",
    "fechaResolucion": "2026-10-17",
    "estado": "rechazada",
    "observaciones": "El solicitante viajaba fuera del país por trabajo durante 6 meses",
    "activo": true,
    "seguimiento": []
  }
]);

// 5. ÍNDICES: Únicos y de búsqueda para optimizar consultas y listar metadatos
db.animales.createIndex({ "idAnimal": 1 }, { unique: true, name: "idx_animal_id_unico" });
db.animales.createIndex({ "especie": 1, "estado": 1 }, { name: "idx_animal_especie_estado" });
db.adoptantes.createIndex({ "dni": 1 }, { unique: true, name: "idx_adoptante_dni_unico" });
db.adopciones.createIndex({ "idAdopcion": 1 }, { unique: true, name: "idx_adopcion_id_unico" });
db.adopciones.createIndex({ "estado": 1 }, { name: "idx_adopcion_estado" });