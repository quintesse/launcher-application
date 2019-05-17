package it

import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URL

fun waitForFirstBuild(part: Part) {
    // First we wait for the first build to appear
    // (if we try to start our build sooner it will silently fail)
    System.out.println("Waiting for build system to ready up")
    for (i in 1..60) {
        try {
            runCmd("oc", "get", "build/" + getServiceName(part) + "-1", "--template={{.status.phase}}")
            System.out.println("ok")
            break
        } catch (ex: Exception) {
            if (ex.localizedMessage.toLowerCase().indexOf("(notfound)") < 0) {
                System.out.println("failed")
                throw ex
            }
            if (!isDryRun()) {
                sleep(5000)
                System.out.println("${i * 5} seconds have passed...")
            }
        }
    }
    // Then we cancel that first build which will fail anyway
    try {
        runCmd("oc", "cancel-build", getServiceName(part) + "-1")
    } catch (ex: Exception) {
        // Ignore any errors
    }
}

fun waitForProject(part: Part) {
    // We wait for the deployment to spin up our application
    waitForAppStart(part)
    // Wait for the app to respond
    waitForAppResponse(part)
}

fun waitForAppStart(part: Part) {
    System.out.println("Waiting for application to start...")
    for (i in 1..60) {
        try {
            runCmd("oc", "wait", "dc/" + getServiceName(part), "--timeout=15s", "--for", "condition=available")
            System.out.println("ok")
            break
        } catch (ex: Exception) {
            if (ex.localizedMessage.toLowerCase().contains("error: timed out")) {
                System.out.println("failed")
                throw ex
            }
            System.out.println("${i * 15} seconds have passed...")
        }
    }
}

fun waitForAppResponse(part: Part) {
    System.out.println("Waiting for application to respond...")
    if (!isDryRun()) {
        for (i in 1..10) {
            val url = "http://${getRouteHost(getServiceName(part))}"
            with(URL(url).openConnection() as HttpURLConnection) {
                if (responseCode in 200 until 500) {
                    System.out.println("ok")
                    return
                }
            }
            sleep(5000)
            System.out.println("${i * 5} seconds have passed...")
        }
    }
}

fun getRouteHost(name: String): String {
    return runCmd("oc", "get", "route", name, "--template", "{{.spec.host}}")
}