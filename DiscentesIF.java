// Representa um aluno: guarda as notas em 12 matérias e simula uma rotina
// de sono que influencia o desempenho acadêmico ao longo do ano letivo.
class DiscentesIF {
    // identificador anônimo do estudante (ex: EST01); a correspondência com
    // o nome real fica em um arquivo separado, fora deste código.
    String codigoEstudante;
    String turma;

    Materia[] materias;

    static String[] NOMES_MATERIAS = {
            "Biologia", "Matemática", "Português e Literatura", "Programação",
            "Banco de Dados", "Projeto Integrador", "Geografia", "Inglês",
            "Química", "Física", "Educação Física", "Sociologia"
    };

    // hábitos de sono do dia atual, atualizados em simularNoite()
    double horasSono;
    int qualidadeSono;      // 0 (péssima) a 10 (ótima)
    int cafeinaDoses;       // doses de cafeína no dia
    int minutosParaDormir;  // latência: tempo até pegar no sono
    int usoCelularMinutos;  // uso de celular antes de dormir (hábito complementar, não é a latência)
    int despertaresNoite;   // vezes que acordou durante a madrugada
    int exercicioFisico;    // 0 (nenhum) a 3 (intenso)
    double horasNaCama;     // sono + latência + tempo acordado
    double eficienciaSono;  // horasSono / horasNaCama * 100 (%)

    // pontuação de cada componente do sono, inspirada no PSQI (0 = melhor, 3 = pior)
    int compQualidadeSubjetiva;
    int compLatencia;
    int compDuracao;
    int compEficiencia;
    int compDisturbios;
    // soma dos 5 componentes acima (0 a 15); é apenas inspirada no PSQI, não
    // a aplicação completa e validada do instrumento (faltam os componentes
    // de uso de medicação e disfunção diurna).
    int indiceSonoInspiradoPSQI;

    // acumuladores usados para calcular a média de sono entre uma avaliação e outra
    double somaHorasSono;
    double somaQualidadeSono;
    double somaEficienciaSono;
    double somaIndiceSonoInspiradoPSQI;
    int diasContadosSono;

    // última média calculada, usada nos relatórios e no cálculo da nota
    double mediaHorasSono;
    double mediaQualidadeSono;
    double mediaEficienciaSono;
    double mediaIndiceSonoInspiradoPSQI;

    // indica se a rotina de sono vem do formulário (valores típicos, com
    // pequenas variações diárias) ou é sorteada livremente
    boolean rotinaManual;

    // valores típicos informados no formulário, usados como referência
    // para a variação diária da rotina de sono
    double horasSonoBase;
    int qualidadeSonoBase;
    int despertaresNoiteBase;
    int minutosParaDormirBase;
    int freqCafeinaSemana;          // dias por semana em que toma cafeína (0 a 7)
    int doseCafeinaTipica;          // doses nos dias em que toma
    int freqExercicioSemana;        // dias por semana em que se exercita (0 a 7)
    int intensidadeExercicioTipica; // intensidade nos dias em que se exercita (0 a 3)

    // usados para converter o dia do ano letivo em uma data aproximada
    static int[] DIAS_POR_MES = {31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31, 30};
    static String[] NOMES_MESES = {"mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez", "jan", "fev"};

    // cria o aluno: valida o código e a turma, define a rotina de sono
    // inicial e valida/arredonda as notas informadas em cada matéria
    public DiscentesIF(String codigoEstudante, String turma, double[] notasIniciais) {
        if (codigoEstudante.length() >= 4) {
            this.codigoEstudante = codigoEstudante.trim();
        } else {
            System.out.printf("Código do estudante inválido%n");
            this.codigoEstudante = "NAO_INFORMADO";
        }
        if (turma.length() > 5) {
            this.turma = turma.trim();
        } else {
            System.out.printf("Turma Inválida%n");
            this.turma = "Não Informada";
        }

        // rotina de sono inicial "padrão"
        horasSono = 8.0;
        qualidadeSono = 7;
        cafeinaDoses = 0;
        usoCelularMinutos = 0;
        despertaresNoite = 0;
        minutosParaDormir = 15;
        exercicioFisico = 0;
        horasNaCama = 8.0;
        eficienciaSono = 100.0;
        rotinaManual = false;

        somaHorasSono = 0;
        somaQualidadeSono = 0;
        somaEficienciaSono = 0;
        somaIndiceSonoInspiradoPSQI = 0;
        diasContadosSono = 0;

        mediaHorasSono = horasSono;
        mediaQualidadeSono = qualidadeSono;
        mediaEficienciaSono = eficienciaSono;
        mediaIndiceSonoInspiradoPSQI = 0;

        materias = new Materia[NOMES_MATERIAS.length];
        for (int i = 0; i < NOMES_MATERIAS.length; i++) {
            double notaInicial = notasIniciais[i];
            if (notaInicial <= 0) {
                System.out.printf("Nota de %s não Inserida%n", NOMES_MATERIAS[i]);
                notaInicial = 7.0;
            } else if (notaInicial > 10) {
                System.out.printf("Nota de %s inválida (valor exorbitante), ajustada para 10.0%n", NOMES_MATERIAS[i]);
                notaInicial = 10.0;
            }
            notaInicial = arredondar1Casa(notaInicial);
            materias[i] = new Materia(NOMES_MATERIAS[i], i, notaInicial);
        }
    }

    // arredonda uma nota para 1 casa decimal
    public static double arredondar1Casa(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    // atalho para usar a mesma nota em todas as matérias
    public DiscentesIF(String codigoEstudante, String turma, double notaBase) {
        this(codigoEstudante, turma, criarArrayRepetido(notaBase));
    }

    private static double[] criarArrayRepetido(double valor) {
        double[] array = new double[NOMES_MATERIAS.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = valor;
        }
        return array;
    }

    // define a rotina de sono do formulário, usada como referência para a
    // variação diária ao longo da simulação
    public void definirRotinaManual(double horas, int qualidade, int despertares,
                                    int minutosParaDormir, int celular,
                                    int freqCafeinaSemana, int doseCafeinaTipica,
                                    int freqExercicioSemana, int intensidadeExercicioTipica) {
        rotinaManual = true;
        horasSonoBase = horas;
        qualidadeSonoBase = qualidade;
        despertaresNoiteBase = despertares;
        minutosParaDormirBase = minutosParaDormir;
        usoCelularMinutos = celular;
        this.freqCafeinaSemana = freqCafeinaSemana;
        this.doseCafeinaTipica = doseCafeinaTipica;
        this.freqExercicioSemana = freqExercicioSemana;
        this.intensidadeExercicioTipica = intensidadeExercicioTipica;
    }

    // simula uma noite de sono e atualiza o índice de sono do dia; chamado
    // uma vez por dia
    public void simularNoite() {
        if (rotinaManual) {
            variarRotinaDoFormulario();
        } else {
            sortearHabitosDoDia();
        }

        double latenciaHoras = usoCelularMinutos / 60.0;
        horasNaCama = horasSono + latenciaHoras + (despertaresNoite * (10.0 / 60.0));
        eficienciaSono = (horasSono / horasNaCama) * 100;
        if (eficienciaSono > 100) eficienciaSono = 100;

        calcularIndiceSonoInspiradoPSQI();

        somaHorasSono += horasSono;
        somaQualidadeSono += qualidadeSono;
        somaEficienciaSono += eficienciaSono;
        somaIndiceSonoInspiradoPSQI += indiceSonoInspiradoPSQI;
        diasContadosSono++;
    }

    // gera a rotina de sono do dia a partir dos valores típicos do
    // formulário, com uma pequena variação diária
    private void variarRotinaDoFormulario() {
        horasSono = horasSonoBase + (Math.random() - 0.5);
        if (horasSono < 3) horasSono = 3;
        if (horasSono > 9) horasSono = 9;

        int variacaoQualidade = (int) (Math.random() * 3) - 1;
        qualidadeSono = qualidadeSonoBase + variacaoQualidade;
        if (qualidadeSono < 0) qualidadeSono = 0;
        if (qualidadeSono > 10) qualidadeSono = 10;

        despertaresNoite = despertaresNoiteBase;
        if (Math.random() < 0.2) despertaresNoite++;

        minutosParaDormir = minutosParaDormirBase;

        double chanceCafeina = freqCafeinaSemana / 7.0;
        cafeinaDoses = (Math.random() < chanceCafeina) ? doseCafeinaTipica : 0;

        double chanceExercicio = freqExercicioSemana / 7.0;
        exercicioFisico = (Math.random() < chanceExercicio) ? intensidadeExercicioTipica : 0;
    }

    // sorteia a rotina de sono do dia (usado quando não há dados de formulário)
    private void sortearHabitosDoDia() {
        // horas e qualidade tendem a voltar para valores médios a cada noite
        qualidadeSono += (int) Math.round((7.0 - qualidadeSono) * 0.25);
        horasSono += (7.5 - horasSono) * 0.25;

        int qualidadeInicioNoite = qualidadeSono;

        double chance = Math.random();
        if (chance < 0.20) {
            horasSono -= 1.5;
            qualidadeSono -= 2;
        } else if (chance < 0.35) {
            horasSono -= 0.5;
            qualidadeSono -= 1;
        } else if (chance < 0.55) {
            horasSono += 0.5;
            qualidadeSono += 1;
        }

        // cafeína: doses altas atrapalham o sono
        cafeinaDoses = (int) (Math.random() * 4); // 0 a 3
        if (cafeinaDoses == 3) {
            horasSono -= 0.4;
            qualidadeSono -= 1;
        }

        // uso de celular: mais de 1 hora antes de dormir reduz a qualidade
        usoCelularMinutos = (int) (Math.random() * 121); // 0 a 120 minutos
        if (usoCelularMinutos > 60) {
            qualidadeSono -= 1;
        }

        // latência: tempo sorteado para pegar no sono
        minutosParaDormir = (int) (Math.random() * 91); // 0 a 90 minutos

        // despertares: chance baseada na qualidade de sono do início da noite
        despertaresNoite = 0;
        for (int i = 0; i < 2; i++) {
            double chanceDespertar = 0.05 + (10 - qualidadeInicioNoite) * 0.01;
            if (Math.random() < chanceDespertar) despertaresNoite++;
        }
        qualidadeSono -= despertaresNoite;

        // exercício: melhora a qualidade do sono
        exercicioFisico = (int) (Math.random() * 4); // 0 a 3
        if (exercicioFisico >= 1) {
            qualidadeSono += 1;
        }

        if (horasSono < 3) horasSono = 3;
        if (horasSono > 9) horasSono = 9;
        if (qualidadeSono < 0) qualidadeSono = 0;
        if (qualidadeSono > 10) qualidadeSono = 10;
    }

    // calcula os 5 componentes do índice de sono (0 a 3 cada, inspirado no
    // PSQI) e soma no índice geral (0 a 15)
    private void calcularIndiceSonoInspiradoPSQI() {
        if (qualidadeSono >= 8) compQualidadeSubjetiva = 0;
        else if (qualidadeSono >= 6) compQualidadeSubjetiva = 1;
        else if (qualidadeSono >= 4) compQualidadeSubjetiva = 2;
        else compQualidadeSubjetiva = 3;

        if (minutosParaDormir <= 15) compLatencia = 0;
        else if (minutosParaDormir <= 30) compLatencia = 1;
        else if (minutosParaDormir <= 60) compLatencia = 2;
        else compLatencia = 3;

        if (horasSono > 7) compDuracao = 0;
        else if (horasSono >= 6) compDuracao = 1;
        else if (horasSono >= 5) compDuracao = 2;
        else compDuracao = 3;

        if (eficienciaSono > 85) compEficiencia = 0;
        else if (eficienciaSono >= 75) compEficiencia = 1;
        else if (eficienciaSono >= 65) compEficiencia = 2;
        else compEficiencia = 3;

        if (despertaresNoite == 0) compDisturbios = 0;
        else if (despertaresNoite == 1) compDisturbios = 1;
        else if (despertaresNoite <= 3) compDisturbios = 2;
        else compDisturbios = 3;

        indiceSonoInspiradoPSQI = compQualidadeSubjetiva + compLatencia + compDuracao
                + compEficiencia + compDisturbios;
    }

    // calcula a média de sono desde a última avaliação e reseta os acumuladores
    public void calcularMediaSono() {
        if (diasContadosSono > 0) {
            mediaHorasSono = somaHorasSono / diasContadosSono;
            mediaQualidadeSono = somaQualidadeSono / diasContadosSono;
            mediaEficienciaSono = somaEficienciaSono / diasContadosSono;
            mediaIndiceSonoInspiradoPSQI = somaIndiceSonoInspiradoPSQI / diasContadosSono;
        }
        somaHorasSono = 0;
        somaQualidadeSono = 0;
        somaEficienciaSono = 0;
        somaIndiceSonoInspiradoPSQI = 0;
        diasContadosSono = 0;
    }

    // transforma o índice médio de sono em um multiplicador que aumenta a
    // variação da nota quando o sono é pior
    public double calcularFatorSono() {
        double fator = 1.0;
        if (mediaIndiceSonoInspiradoPSQI > 11) {
            fator += 0.6; // muito ruim
        } else if (mediaIndiceSonoInspiradoPSQI > 7) {
            fator += 0.4; // ruim
        } else if (mediaIndiceSonoInspiradoPSQI > 3) {
            fator += 0.2; // baixa qualidade
        }
        return fator;
    }

    // gera a nota de uma avaliação, a partir da nota base da matéria e do sono acumulado
    public double gerarNota(Materia m) {
        double base = m.notaBase;
        double faixa;

        if (base >= 9) {
            faixa = 0.8;
        } else if (base >= 8) {
            faixa = 1.2;
        } else if (base >= 6) {
            faixa = 2.0;
        } else {
            faixa = 3.0;
        }

        calcularMediaSono();
        double fatorSono = calcularFatorSono();
        faixa = faixa * fatorSono;

        double variacao = (Math.random() * faixa) - (faixa / 2);
        if (mediaQualidadeSono < 5) {
            variacao -= (5 - mediaQualidadeSono) * 0.1;
        }

        double nota = base + variacao;
        if (nota > 10) nota = 10;
        if (nota < 0) nota = 0;

        return arredondar1Casa(nota);
    }

    // converte o dia do ano letivo (1 a 180) em uma data aproximada,
    // contando a partir de 1 de março, sem pular fins de semana
    private String formatarData(int dia) {
        int diaRestante = dia;
        int mesIndice = 0;
        while (diaRestante > DIAS_POR_MES[mesIndice]) {
            diaRestante -= DIAS_POR_MES[mesIndice];
            mesIndice++;
        }
        return String.format("%02d/%s", diaRestante, NOMES_MESES[mesIndice]);
    }

    // percorre as 12 matérias todo dia, verificando se é dia de trabalho ou prova
    public void simularAno() {
        for (int dia = 1; dia <= 180; dia++) {
            simularNoite();

            int trimestre = ((dia - 1) / 60) + 1;
            int diaNoTrimestre = ((dia - 1) % 60) + 1;

            for (Materia m : materias) {
                if (diaNoTrimestre == m.diaTrabalho) {
                    if (trimestre == 1) {
                        // a nota dada/pedida no início já vale como T1
                        calcularMediaSono();
                        imprimirAvaliacao("trabalho", m.nome, trimestre, dia, m.trabalhoT1);
                    } else {
                        double nota = gerarNota(m);
                        if (trimestre == 2) m.trabalhoT2 = nota;
                        else m.trabalhoT3 = nota;
                        imprimirAvaliacao("trabalho", m.nome, trimestre, dia, nota);
                    }
                }
                if (diaNoTrimestre == m.diaProva) {
                    if (trimestre == 1) {
                        // a nota dada/pedida no início já vale como T1
                        calcularMediaSono();
                        imprimirAvaliacao("prova", m.nome, trimestre, dia, m.provaT1);
                    } else {
                        double nota = gerarNota(m);
                        if (trimestre == 2) m.provaT2 = nota;
                        else m.provaT3 = nota;
                        imprimirAvaliacao("prova", m.nome, trimestre, dia, nota);
                    }
                }
            }

            if (dia == 60) {
                for (Materia m : materias) m.notaBase = m.mediaT1();
            }
            if (dia == 120) {
                for (Materia m : materias) m.notaBase = m.mediaT2();
            }
        }
    }

    // imprime a nota da avaliação e um resumo do sono usado para calculá-la
    private void imprimirAvaliacao(String tipo, String materia, int trimestre, int dia, double nota) {
        System.out.printf("\n %s fez %s de %s (T%d) em %s: %.1f%n",
                codigoEstudante, tipo, materia, trimestre, formatarData(dia), nota);
        System.out.printf("   sono (média do período) -> qualidade %.1f | eficiência %.0f%% | índice inspirado no PSQI %.1f de 15%n",
                mediaQualidadeSono, mediaEficienciaSono, mediaIndiceSonoInspiradoPSQI);
        System.out.printf("   última noite -> cafeína: %d dose(s) | celular: %d min | tempo p/ adormecer: %d min | despertares: %d | exercício: %d%n",
                cafeinaDoses, usoCelularMinutos, minutosParaDormir, despertaresNoite, exercicioFisico);

        // saída interpretativa: mostra o que mais pesou no índice, não só o número
        System.out.printf("   índice de sono simulado (última noite, inspirado no PSQI): %d de 15%n ",
                indiceSonoInspiradoPSQI);
        System.out.println("   principais fatores:");
        System.out.print(interpretarComponentes());
    }

    // lista os componentes que contribuíram (pontuação > 0) para o índice
    // da última noite, para explicar o número em vez de só mostrá-lo
    private String interpretarComponentes() {
        StringBuilder sb = new StringBuilder();
        if (compQualidadeSubjetiva > 0) {
            sb.append(String.format("      - Qualidade subjetiva do sono: %d ponto(s)%n", compQualidadeSubjetiva));
        }
        if (compLatencia > 0) {
            sb.append(String.format("      - Latência do sono (tempo p/ adormecer): %d ponto(s)%n", compLatencia));
        }
        if (compDuracao > 0) {
            sb.append(String.format("      - Duração do sono: %d ponto(s)%n", compDuracao));
        }
        if (compEficiencia > 0) {
            sb.append(String.format("      - Eficiência do sono: %d ponto(s)%n", compEficiencia));
        }
        if (compDisturbios > 0) {
            sb.append(String.format("      - Distúrbios do sono (despertares): %d ponto(s)%n", compDisturbios));
        }
        if (sb.length() == 0) {
            sb.append(String.format("      - Nenhum fator relevante (sono dentro do esperado)%n"));
        }
        System.out.printf("\n");
        return sb.toString();
    }

    // mostra as notas de cada matéria e a média geral de cada trimestre
    public void exibir() {
        System.out.printf("%nCódigo do estudante: %s%nTurma: %s%n%n", codigoEstudante, turma);

        double somaGeralT1 = 0, somaGeralT2 = 0, somaGeralT3 = 0;

        for (Materia m : materias) {
            System.out.printf("%s%n", m.nome);
            System.out.printf(" T1 -> Trabalho: %.1f | Prova: %.1f | Média: %.1f%n", m.trabalhoT1, m.provaT1, m.mediaT1());
            System.out.printf(" T2 -> Trabalho: %.1f | Prova: %.1f | Média: %.1f%n", m.trabalhoT2, m.provaT2, m.mediaT2());
            System.out.printf(" T3 -> Trabalho: %.1f | Prova: %.1f | Média: %.1f%n%n", m.trabalhoT3, m.provaT3, m.mediaT3());

            somaGeralT1 += m.mediaT1();
            somaGeralT2 += m.mediaT2();
            somaGeralT3 += m.mediaT3();
        }

        double mediaGeralT1 = somaGeralT1 / materias.length;
        double mediaGeralT2 = somaGeralT2 / materias.length;
        double mediaGeralT3 = somaGeralT3 / materias.length;
        double mediaFinalGeral = (mediaGeralT1 + mediaGeralT2 + mediaGeralT3) / 3;

        System.out.printf("Média Geral T1: %.1f%n", mediaGeralT1);
        System.out.printf("Média Geral T2: %.1f%n", mediaGeralT2);
        System.out.printf("Média Geral T3: %.1f%n", mediaGeralT3);
        System.out.printf("Média Final do Ano: %.1f%n", mediaFinalGeral);
    }

    public static void main(String[] args) {
        DiscentesIF[] info2t = new DiscentesIF[32];

        // identificação apenas por código; os nomes reais ficam em arquivo separado
        double[] notasEst01 = {8.0, 6.5, 7.0, 9.0, 8.5, 9.5, 6.0, 7.5, 5.5, 6.0, 9.0, 8.0};
        info2t[0] = new DiscentesIF("EST01", "Info2T", notasEst01);

        info2t[1] = new DiscentesIF("EST02", "Info2T", 7.0);
        info2t[2] = new DiscentesIF("EST03", "Info2T", 6.7);
        info2t[3] = new DiscentesIF("EST04", "Info2T", 4.0);
        info2t[4] = new DiscentesIF("EST05", "Info2T", 3.0);
        info2t[5] = new DiscentesIF("EST06", "Info2T", 2.5);
        info2t[6] = new DiscentesIF("EST07", "Info2T", 1.1);
        info2t[7] = new DiscentesIF("EST08", "Info2T", 5.0);
        info2t[8] = new DiscentesIF("EST09", "Info2T", 0.0);
        info2t[9] = new DiscentesIF("EST10", "Info2T", 10.0);
        info2t[10] = new DiscentesIF("EST11", "Info2T", 4.2);
        info2t[11] = new DiscentesIF("EST12", "Info2T", 0);
        info2t[12] = new DiscentesIF("EST13", "Info2T", 3.8);
        info2t[13] = new DiscentesIF("EST14", "Info2T", 4.4);
        info2t[14] = new DiscentesIF("EST15", "Info2T", 0.8);
        info2t[15] = new DiscentesIF("EST16", "Info2T", 8.0);
        info2t[16] = new DiscentesIF("EST17", "Info2T", 9.8);
        info2t[17] = new DiscentesIF("EST18", "Info2T", 9.0);
        info2t[18] = new DiscentesIF("EST19", "Info2T", 8.9);
        info2t[19] = new DiscentesIF("EST20", "Info2T", 0);
        info2t[20] = new DiscentesIF("EST21", "Info2T", 8.0);
        info2t[21] = new DiscentesIF("EST22", "Info2T", 6.7);
        info2t[22] = new DiscentesIF("EST23", "Info2T", 7.0);
        info2t[23] = new DiscentesIF("EST24", "Info2T", 5.6);
        info2t[24] = new DiscentesIF("EST25", "Info2T", 0.5);
        info2t[25] = new DiscentesIF("EST26", "Info2T", 4.4);
        info2t[26] = new DiscentesIF("EST27", "Info2T", 6.9);
        info2t[27] = new DiscentesIF("EST28", "Info2T", 7.5);
        info2t[28] = new DiscentesIF("EST29", "Info2T", 10.0);
        info2t[29] = new DiscentesIF("EST30", "Info2T", 5.5);
        info2t[30] = new DiscentesIF("EST31", "Info2T", 10.0);
        info2t[31] = new DiscentesIF("EST32", "Info2T", 6.77);

        for (DiscentesIF turmainfo2 : info2t) {
            turmainfo2.simularAno();
            turmainfo2.exibir();
        }
    }
}
