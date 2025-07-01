package src.erp;

public class Usuario {
    private int id;
    private String nome;
    private String cpf;
    private String senha;
    private String perfil;

    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Usuario(int id, String nome, String cpf, String senha, String perfil) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.perfil = perfil;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getSenha() {
        return senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return nome;
    }
}
