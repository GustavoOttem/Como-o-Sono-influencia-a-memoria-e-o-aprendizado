import java.util.Scanner;

// Monta um aluno com base nas respostas do usuário: uma nota por matéria e
// a rotina de sono dele. Simula o ano letivo usando essa rotina como base.
class FormularioAluno {

    // lê um double do Scanner, repetindo a pergunta até vir um valor
    // numérico dentro do intervalo [min, max]
    private static double lerDouble(Scanner leitor, String pergunta, double min, double max) {
        double valor;
        while (true) {
            System.out.print(pergunta);
            try {
                valor = Double.parseDouble(leitor.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número.");
                continue;
            }
            if (valor < min || valor > max) {
                System.out.printf("Valor fora da faixa permitida (%.1f a %.1f)! Tente novamente.%n", min, max);
                continue;
            }
            break;
        }
        return valor;
    }

    // lê um int do Scanner, repetindo a pergunta até vir um valor
    // numérico dentro do intervalo [min, max]
    private static int lerInt(Scanner leitor, String pergunta, int min, int max) {
        int valor;
        while (true) {
            System.out.print(pergunta);
            try {
                valor = Integer.parseInt(leitor.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número inteiro.");
                continue;
            }
            if (valor < min || valor > max) {
                System.out.printf("Valor fora da faixa permitida (%d a %d)! Tente novamente.%n", min, max);
                continue;
            }
            break;
        }
        return valor;
    }

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("=== Formulário de simulação de aluno ===");

        // identificação por código, não por nome real
        System.out.print("Código do estudante (ex: EST01): ");
        String codigoEstudante = leitor.nextLine();

        System.out.print("Turma: ");
        String turma = leitor.nextLine();

        System.out.println();
        System.out.println("Nota atual / 1° Trimestre em cada matéria (0 a 10):");
        double[] notas = new double[DiscentesIF.NOMES_MATERIAS.length];
        for (int i = 0; i < notas.length; i++) {
            double nota = lerDouble(leitor, DiscentesIF.NOMES_MATERIAS[i] + ": ", 0, 10);
            notas[i] = DiscentesIF.arredondar1Casa(nota);
        }

        System.out.println();
        System.out.println("Agora a rotina de sono do aluno (valores típicos; a simulação varia um pouco em cima disso):");

        double horasSono = lerDouble(leitor, "Quantas horas dorme por noite, em média? ", 0.1, 24);

        int qualidadeSono = lerInt(leitor, "Qualidade do sono percebida, de 0 (péssima) a 10 (ótima)? ", 0, 10);

        int despertaresNoite = lerInt(leitor, "Quantas vezes costuma acordar durante a madrugada? ", 0, 20);

        // latência do sono, separada do uso de celular
        int minutosParaDormir = lerInt(leitor, "Quanto tempo, em minutos, você geralmente leva para adormecer? ", 0, 1440);

        int usoCelularMinutos = lerInt(leitor, "Quantos minutos usa o celular antes de dormir? ", 0, 1440);

        int freqCafeinaSemana = lerInt(leitor, "Em quantos dias por semana toma cafeína (café, energético, etc.)? (0 a 7) ", 0, 7);

        int doseCafeinaTipica = lerInt(leitor, "Nos dias em que toma, quantas doses costuma tomar? ", 0, 20);

        int freqExercicioSemana = lerInt(leitor, "Em quantos dias por semana se exercita? (0 a 7) ", 0, 7);

        int intensidadeExercicioTipica = lerInt(leitor, "Nos dias em que se exercita, qual a intensidade? 0 (leve) a 3 (intenso) ", 0, 3);

        DiscentesIF aluno = new DiscentesIF(codigoEstudante, turma, notas);
        aluno.definirRotinaManual(horasSono, qualidadeSono, despertaresNoite, minutosParaDormir, usoCelularMinutos,
                freqCafeinaSemana, doseCafeinaTipica,
                freqExercicioSemana, intensidadeExercicioTipica);

        aluno.simularAno();
        aluno.exibir();

        leitor.close();
    }
}
