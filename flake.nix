{
  description = "Scalus UPLC-CAPE Submissions";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            # Scala build tools
            sbt
            scala_3

            # JDK
            jdk17

            # Utilities for UPLC-CAPE submissions
            jq

            # Node.js for uplc-cli if needed
            nodejs_20

            # Custom build script
            (pkgs.writeShellScriptBin "build-scalus" ''
              echo "Building all Scalus UPLC programs..."
              sbt "runMain fibonacci_naive_recursion.compileFibonacciNaiveRecursion" && \
              sbt "runMain fibonacci.compileFibonacci" && \
              sbt "runMain fibonacci_prepacked.compileFibonacciPrepacked" && \
              sbt "runMain factorial_naive_recursion.compileFactorialNaiveRecursion" && \
              sbt "runMain factorial.compileFactorial"
            '')
          ];

          shellHook = ''
            echo "Scalus UPLC-CAPE Development Environment"
            echo "Scala version: $(scala -version 2>&1)"
            echo "sbt version: $(sbt --version 2>&1 | grep 'sbt version')"
            echo ""
            echo "Available commands:"
            echo "  build-scalus      - Build all Scalus UPLC programs"
            echo "  sbt compile       - Compile Scala sources"
            echo "  sbt clean         - Clean build artifacts"
          '';
        };
      }
    );
}
