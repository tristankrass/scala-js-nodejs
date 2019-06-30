package io.scalajs.nodejs.cluster

import io.scalajs.JSON
import io.scalajs.nodejs.setTimeout
import io.scalajs.util.DurationHelper._
import org.scalatest.FunSpec

import scala.concurrent.duration._
import scala.scalajs.js

/**
  * Cluster Tests
  */
class ClusterTest extends FunSpec {

  describe("Cluster") {

    it("cluster should be master") {
      info(s"cluster.isMaster => ${Cluster.isMaster}")
      assert(Cluster.isMaster)
    }

    it("cluster should not be a worker") {
      info(s"cluster.isWorker => ${Cluster.isWorker}")
      assert(!Cluster.isWorker)
    }

    it("cluster.schedulingPolicy must be defined") {
      info(s"cluster.schedulingPolicy => ${Cluster.schedulingPolicy}")
      assert(!js.isUndefined(Cluster.schedulingPolicy))
    }

    it("cluster.settings  must be defined") {
      info(s"cluster.settings => ${JSON.stringify(Cluster.settings)}")
      assert(!js.isUndefined(Cluster.settings))
    }

    // TODO: Update test
    // Cluster.fork() behavior changed somewhere between Node v10 and v12.
    // TypeError [ERR_INVALID_ARG_TYPE]: The "modulePath" argument must be of type string. Received type undefined
    //    at validateString (internal/validators.js:107:11)
    //    at fork (child_process.js:55:3)
    //    at createWorkerProcess (internal/cluster/master.js:130:10)
    //    at EventEmitter.cluster.fork (internal/cluster/master.js:164:25)
    //    at repl:1:9
    //    at Script.runInThisContext (vm.js:123:20)
    //    at REPLServer.defaultEval (repl.js:384:29)
    //    at bound (domain.js:415:14)
    //    at REPLServer.runBound [as eval] (domain.js:428:12)
    //    at REPLServer.onLine (repl.js:700:10)
    ignore("cluster support fork() new workers") {
      if (Cluster.isMaster) {
        // Fork the workers
        (1 to 2) map { n =>
          val worker = Cluster.fork()
          worker.onOnline(() => info(s"Worker #$n is online..."))

          // shutdown worker after 5 seconds
          setTimeout(() => {
            info(s"Shutting down worker ${worker.id}...")
            worker.disconnect()
          }, 0.5.seconds)
        }

        Cluster.onExit((worker, code, signal) => info(s"worker ${worker.process.pid} died"))
      }
    }

  }

}
