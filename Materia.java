// Representa uma matéria cursada por um aluno: guarda as notas de cada
// trimestre e os dias do ano letivo em que ela tem trabalho e prova.
class Materia {
    String nome;

    // dia do trimestre (1 a 60) em que a matéria tem trabalho e prova;
    // nenhum dia se repete entre as matérias
    int diaTrabalho;
    int diaProva;
    int indice;

    // nota de referência usada para gerar as notas seguintes: no T1 é a
    // nota inicial do aluno; depois de cada trimestre, vira a média tirada
    // naquele trimestre
    double notaBase;

    double trabalhoT1, provaT1;
    double trabalhoT2, provaT2;
    double trabalhoT3, provaT3;

    public Materia(String nome, int indice, double notaBase) {
        this.nome = nome;
        this.notaBase = notaBase;

        // a nota informada inicialmente já vale como o 1° Trimestre
        this.trabalhoT1 = notaBase;
        this.provaT1 = notaBase;

        this.diaTrabalho = (indice + 1) * 4;
        this.diaProva = 30 + (indice + 1) * 2;
    }

    public double mediaT1() {
        return (trabalhoT1 + provaT1) / 2;
    }

    public double mediaT2() {
        return (trabalhoT2 + provaT2) / 2;
    }

    public double mediaT3() {
        return (trabalhoT3 + provaT3) / 2;
    }

    public double mediaFinal() {
        return (mediaT1() + mediaT2() + mediaT3()) / 3;
    }
}
