package com.esail.serverAlarma.dto;

import java.time.LocalTime;

public class HorarioPlantillaRequestDTO {

    private LocalTime horaInicio;
    private LocalTime horaAlmuerzo;
    private LocalTime horaVuelta;
    private LocalTime horaFin;
    private Boolean activo;

    public HorarioPlantillaRequestDTO() {}

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraAlmuerzo() { return horaAlmuerzo; }
    public void setHoraAlmuerzo(LocalTime horaAlmuerzo) { this.horaAlmuerzo = horaAlmuerzo; }

    public LocalTime getHoraVuelta() { return horaVuelta; }
    public void setHoraVuelta(LocalTime horaVuelta) { this.horaVuelta = horaVuelta; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}