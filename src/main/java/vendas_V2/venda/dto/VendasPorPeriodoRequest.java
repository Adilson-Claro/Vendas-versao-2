package vendas_V2.venda.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VendasPorPeriodoRequest(@NotNull String dataInicio,
                                      @NotNull String dataFim) {
    public LocalDate getDataInicio() {
        return LocalDate.parse(dataInicio, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public LocalDate getDataFim() {
        return LocalDate.parse(dataFim, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}