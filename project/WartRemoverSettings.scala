import play.sbt.routes.RoutesKeys.routes
import sbt.{Def, *}
import sbt.Keys.*
import wartremover.Wart
import wartremover.WartRemover.autoImport.*

object WartRemoverSettings {
  val wartRemoverSettings: Seq[Def.Setting[? >: Seq[Wart] & Task[Seq[File]] <: Equals]] =
    Seq(
      (Compile / compile / wartremoverErrors) ++= {
        if (StrictBuilding.strictBuilding.value) {
          Warts.allBut(
            Wart.DefaultArguments,
            Wart.ImplicitParameter,
            Wart.Nothing,
            Wart.Throw,
            Wart.ToString,
            Wart.PlatformDefault
          )
        } else Nil
      },
      Test / compile / wartremoverErrors --= Seq(
        Wart.Any,
        Wart.Equals,
        Wart.GlobalExecutionContext,
        Wart.NonUnitStatements,
        Wart.PublicInference
      ),
      wartremoverExcluded ++= (Compile / routes).value ++
        target.value.get // stops a weird wart remover Null error being thrown, we don't care about target directory
    )
}