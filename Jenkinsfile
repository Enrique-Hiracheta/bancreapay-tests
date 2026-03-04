pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    parameters {
        string(
            name: 'BRANCH',
            defaultValue: 'main',
            description: 'Rama del repositorio a ejecutar'
        )
        string(
            name: 'TEST_SUITE',
            defaultValue: 'src/test/resources/testng.xml',
            description: 'Archivo de suite TestNG (relativo a la raíz del proyecto)'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Navegador a usar en Selenium Grid'
        )
        string(
            name: 'SELENIUM_HUB_URL',
            defaultValue: 'http://selenium-hub:4444/wd/hub',
            description: 'URL del Selenium Grid Hub'
        )
    }

    environment {
        // Variables de entorno disponibles en todos los stages
        MAVEN_OPTS = '-Xmx1024m'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "📥 Descargando código de la rama: ${params.BRANCH}"
                git branch: "${params.BRANCH}",
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/Enrique-Hiracheta/bancreapay-tests' // ← reemplaza esto
            }
        }

        stage('Verify Environment') {
            steps {
                echo '🔍 Verificando entorno...'
                sh 'mvn --version'
                sh 'java -version'
                echo "🌐 Selenium Hub: ${params.SELENIUM_HUB_URL}"
                echo "🧪 Suite: ${params.TEST_SUITE}"
                echo "🖥️  Browser: ${params.BROWSER}"
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Compilando el proyecto...'
                sh 'mvn clean compile -q'
            }
        }

        stage('Run Tests') {
            steps {
                echo '🚀 Ejecutando tests con TestNG...'
                sh """
                    mvn test \
                        -Dsurefire.suiteXmlFiles=${params.TEST_SUITE} \
                        -Dbrowser=${params.BROWSER} \
                        -Dselenium.hub.url=${params.SELENIUM_HUB_URL} \
                        -Dheadless=true
                """
            }
        }
    }

    post {
        always {
            echo '📊 Publicando resultados...'
            // Reportes JUnit (compatible con Squash TM)
            junit testResults: '**/target/surefire-reports/*.xml',
                  allowEmptyResults: true
            // Reporte TestNG nativo
            testNG reportFilenamePattern: '**/target/surefire-reports/testng-results.xml'
        }
        success {
            echo '✅ Todos los tests pasaron correctamente.'
        }
        failure {
            echo '❌ Hubo fallos en la ejecución. Revisa los reportes.'
        }
        unstable {
            echo '⚠️ La ejecución terminó con tests fallidos.'
        }
        cleanup {
            // Limpia el workspace al final para no acumular basura
            cleanWs()
        }
    }
}