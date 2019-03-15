package polynote.server
import java.io.File
import java.net.URL

import cats.effect.IO
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory
import polynote.config.PolynoteConfig
import polynote.kernel.{KernelStatusUpdate, PolyKernel, SparkPolyKernel}
import polynote.kernel.lang.LanguageInterpreter
import polynote.kernel.util.Publish
import polynote.messages.Notebook

import scala.reflect.io.AbstractFile
import scala.tools.nsc.Settings

object SparkServer extends Server {
  override protected def kernelFactory(config: PolynoteConfig): KernelFactory[IO] = new IOKernelFactory(Map("scala" -> dependencyFetcher), interpreters, config) {
    override protected def mkKernel(
      getNotebook: () => IO[Notebook],
      deps: Map[String, List[(String, File)]],
      subKernels: Map[String, LanguageInterpreter.Factory[IO]],
      statusUpdates: Publish[IO, KernelStatusUpdate],
      extraClassPath: List[File],
      settings: Settings,
      outputDir: AbstractFile,
      parentClassLoader: ClassLoader
    ): IO[PolyKernel] = IO.pure(SparkPolyKernel(getNotebook, deps, subKernels, statusUpdates, extraClassPath, settings, parentClassLoader, config))
  }
}

