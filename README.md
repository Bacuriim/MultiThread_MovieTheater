# 🎬 CinemaThread

Este projeto simula a exibição de um filme em uma sala de cinema com controle de acesso baseado em **threads** e **semáforos**. Fãs (representados por threads) entram na sala até atingir a capacidade máxima, assistem ao filme, fazem uma pausa (coffee break) e então retornam ao ciclo.

## ✅ Requisitos Técnicos

Para executar este projeto, você precisará:

### ☕ Java

- **Java JDK**: Versão **17 ou superior**
  - [Download do JDK](https://www.oracle.com/java/technologies/javase-downloads.html)

### 🎭 JavaFX

- **JavaFX SDK**: Versão **21**
  - [Download do JavaFX](https://gluonhq.com/products/javafx/)
  - [Documentação Oficial do JavaFX](https://openjfx.io/)

### 🧰 Outras Dependências

- Nenhuma biblioteca externa é necessária além do JavaFX (caso use interface gráfica no futuro).
- Se você for executar o projeto via linha de comando ou IDE (como IntelliJ, Eclipse ou VS Code), certifique-se de:
  - Adicionar o JavaFX ao classpath/module-path.
  - Definir as opções de VM corretamente (em projetos com GUI JavaFX).

## 🏗️ Execução

1. Compile e execute o arquivo `CinemaThread.java`.
2. Você pode criar instâncias de `Fan` e iniciar o `Demonstrator` manualmente no `main`.
3. O sistema controla o número de fãs que podem assistir ao filme simultaneamente.

## 📂 Estrutura

- `CinemaThread.java` – Contém a lógica principal com as classes `Demonstrator` e `Fan`.

## 🔧 Sugestão de Execução
 Para gerar o jar utilize:

  ```java --module-path C:\javafx-sdk-17.0.13\lib --add-modules javafx.controls,javafx.fxml -jar cinema_multi_thread jar java --module-path C:\javafx-sdk-17.0.13\lib --add-modules javafx.controls,javafx.fxml -jar cinema_multi_thread.jar ```

  depois:
  ```xcopy src\main\resources out\ /E /I /Y ```

  e por fim:
  ```jar cvfm cinema_multi_thread.jar out\META-INF\MANIFEST.MF -C out .```

  e para executar utilize:
  ```java --module-path C:\javafx-sdk-17.0.13\lib --add-modules javafx.controls,javafx.fxml -jar cinema_multi_thread.jar```

