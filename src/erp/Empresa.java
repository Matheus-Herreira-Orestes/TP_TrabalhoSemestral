package src.erp;

public class Empresa {
    private int id;
    private String razao;
    private String cnpj;
    private String telefone;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;

    public Empresa(int id, String razao, String cnpj, String telefone, String endereco,
                   String bairro, String cidade, String estado) {
        this.id = id;
        this.razao = razao;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public Empresa(int id, String razao) {
        this.id = id;
        this.razao = razao;
    }

    public int getId() {
        return id;
    }

    public String getRazao() {
        return razao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return razao;
    }
}
