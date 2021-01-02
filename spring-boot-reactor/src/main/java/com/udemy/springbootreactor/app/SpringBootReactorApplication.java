package com.udemy.springbootreactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import com.udemy.springbootreactor.app.model.Comentario;
import com.udemy.springbootreactor.app.model.Usuario;
import com.udemy.springbootreactor.app.model.UsuarioComentario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// ejemploIterable();
		// ejemploFlatMap();
		// ejemploToString();
		// ejemploCollectList();
		// ejemploUsuarioComentarioFlatMap();
		// ejemploUsuarioComentarioZipWith();
		// ejemploUsuarioComentarioZipWith2();
		// ejemploZipWithRange();
		// ejemploInterval();
		// ejemploDelayElemnt();
		// ejemploIntervalInfinite();
		// ejemploIntervalInfinite();
		// ejemploIntervalCreate();
		ejemploContraPresion();
	}

	public void ejemploContraPresion() {
		Flux.range(1, 15)
		.log()
		.limitRate(3)
		.subscribe(
			/*
			new Subscriber<Integer>() {

				private Subscription subscriber;
				private Integer limite = 3;
				private Integer consumido = 0;

				@Override
				public void onSubscribe(Subscription s) {
					this.subscriber=s;
					subscriber.request(limite);
				}

				@Override
				public void onNext(Integer t) {
					LOG.info("onNext LOG:" + t);
					consumido++;
					if(consumido == limite){
						consumido = 0;
						subscriber.request(limite);
					}
				}

				@Override
				public void onError(Throwable t) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onComplete() {
					// TODO Auto-generated method stub

				}}
				
				*/
				);
	}

	public void ejemploIntervalCreate(){

		Flux.create(emitter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				private Integer counter = 0;
				@Override
				public void run() {
					emitter.next(++counter);
					if (counter >= 10) {
						timer.cancel();
						emitter.complete();
					}

					if(counter == 8){
						timer.cancel();
						emitter.error(new InterruptedException("Error de interrupcion"));
					}
				}
			}, 1000, 1000);
		})
		.doOnNext(value -> LOG.info(value.toString()))
		.doOnComplete(() -> LOG.info("FLUJO TERMINADO"))
		.subscribe(
			item -> LOG.info(item.toString()),
			error -> LOG.error(error.getMessage(), error),
			() -> LOG.info("FLUJO COMPLETADO"));

	}

	public void ejemploIntervalInfinite() throws InterruptedException {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		Flux.interval(Duration.ofSeconds(1))
		.doOnTerminate(countDownLatch::countDown)
		.flatMap(i -> {
			if(i >= 5){
				return Flux.error(new InterruptedException("Solo hasta 5"));
			} 
			return Flux.just(i);
		})
		.map(item -> "Hola " + item)
		.retry(2)
		.subscribe(LOG::info,error -> LOG.error(error.getMessage(), error));

		countDownLatch.await();
	}

	public void ejemploDelayElemnt() throws InterruptedException {
		Flux.range(1, 12)
		.delayElements(Duration.ofSeconds(1))
		.doOnNext(item -> LOG.info(item.toString()))
		.subscribe();
		Thread.sleep(13000); //Se duerme el hilo de la ejecución para que imprima en consola
	}

	public void ejemploInterval(){
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		rango.zipWith(retraso, (flujoRange, flujoRetraso) -> flujoRange)
		.doOnNext(item -> LOG.info(item.toString()))
		.blockLast(); // Se crea el cloqueo para visualizar en consola
	}

	public void ejemploZipWithRange() {
		Flux<Integer> flujoFiltro = Flux.range(0, 2);
		Flux.just(1,2,3,4)
		.map(i -> (i*2))
		.zipWith(
			flujoFiltro, 
			(itemFlux1, itemFlux2) -> String.format("Primer Flux: %d, Segundo Flux: %d", itemFlux1, itemFlux2))
		.subscribe( texto -> LOG.info(texto));
	}

	public void ejemploUsuarioComentarioZipWith2() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jonathan", "Duque"));
		Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
			Comentario comentario = new Comentario();
			comentario.agregarComentario("Hola Mundo!");
			comentario.agregarComentario("Hola que tal!");
			comentario.agregarComentario("Estoy haciendo un curso reactivo");
			return comentario;
		});

		Mono<UsuarioComentario> usuarioComentrio = usuarioMono
		.zipWith(comentarioMono)
		.map(tupla -> {
			Usuario usuario = tupla.getT1();
			Comentario comentario = tupla.getT2();
			return new UsuarioComentario(usuario, comentario);
		});

		usuarioComentrio.subscribe(uc -> LOG.info(uc.toString()));
	}

	public void ejemploUsuarioComentarioZipWith() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jonathan", "Duque"));
		Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
			Comentario comentario = new Comentario();
			comentario.agregarComentario("Hola Mundo!");
			comentario.agregarComentario("Hola que tal!");
			comentario.agregarComentario("Estoy haciendo un curso reactivo");
			return comentario;
		});

		Mono<UsuarioComentario> usuarioComentrio = usuarioMono.zipWith(comentarioMono,
				(usuario, comentario) -> new UsuarioComentario(usuario, comentario));

		usuarioComentrio.subscribe(uc -> LOG.info(uc.toString()));
	}

	public void ejemploUsuarioComentarioFlatMap(){

		// Mono<Usuario> usuarioMono = Mono.just(new Usuario("Jonathan","Duque"));
		// Mono<Usuario> usuarioMono = Mono.fromCallable(() -> crearUsuario());
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jonathan","Duque"));
		Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
			Comentario comentario = new Comentario();
			comentario.agregarComentario("Hola Mundo!");
			comentario.agregarComentario("Hola que tal!");
			comentario.agregarComentario("Estoy haciendo un curso reactivo");
			return comentario;
		});

		usuarioMono.flatMap(u -> comentarioMono.map(c -> new UsuarioComentario(u, c)))
		.subscribe(uc -> LOG.info(uc.toString()));

	}

	// public Usuario crearUsuario(){
	// 	return new Usuario("Jonathan","Duque");
	// }

	public void ejemploCollectList() throws Exception {
		
		List<Usuario> lUsuarios = new ArrayList<>();
		lUsuarios.add(new Usuario("Jonathan","Duque"));
		lUsuarios.add(new Usuario("Luz","Ramos"));
		lUsuarios.add(new Usuario("Adriana","Garcia"));

		Flux.fromIterable(lUsuarios)
		.collectList()
		.subscribe(listUsuaios -> { 
			listUsuaios.forEach(usuario -> LOG.info(usuario.getNombre()));
		});
	}

	public void ejemploToString() throws Exception {
		
		List<Usuario> lUsuarios = new ArrayList<>();
		lUsuarios.add(new Usuario("Jonathan","Duque"));
		lUsuarios.add(new Usuario("Luz","Ramos"));
		lUsuarios.add(new Usuario("Adriana","Garcia"));

		Flux.fromIterable(lUsuarios)
		.map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
		.flatMap(nombreCompleto -> {
			if(nombreCompleto.contains("Jonathan".toUpperCase())){
				return Mono.just(nombreCompleto);
			} else {
				return Mono.empty();
			}
		})
		.map(nombreCompleto -> {
			return nombreCompleto.toLowerCase();
		})
		.subscribe(u -> LOG.info(u));
	}

	public void ejemploFlatMap() throws Exception {
		
		List<String> lUsuarios = new ArrayList<>();
		lUsuarios.add("Jonathan Duque");
		lUsuarios.add("Luz Ramos");
		lUsuarios.add("Adriana Garcia");

		// Flux<String> flxNombres = Flux.just("Jonathan Duque", "Luz Ramos", "Adriana Garcia");
		Flux.fromIterable(lUsuarios)
		.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
		.flatMap(usuario -> {
			if(usuario.getNombre().equalsIgnoreCase("Jonathan")){
				return Mono.just(usuario);
			} else {
				return Mono.empty();
			}
		})
		.doOnNext(usuario -> {
			if (usuario == null) {
				throw new RuntimeException("Nombre vacío");
			} 
			System.out.println(usuario.getNombre() + " " + usuario.getApellido());
		})
		.map(usuario -> {
			String nombreLC = usuario.getNombre().toLowerCase();
			usuario.setNombre(nombreLC);
			return usuario;
		})
		.subscribe(elemento -> LOG.info(elemento.toString()));
	}

	public void ejemploIterable() throws Exception {
		
		List<String> lUsuarios = new ArrayList<>();
		lUsuarios.add("Jonathan Duque");
		lUsuarios.add("Luz Ramos");
		lUsuarios.add("Adriana Garcia");

		// Flux<String> flxNombres = Flux.just("Jonathan Duque", "Luz Ramos", "Adriana Garcia");
		Flux<String> flxNombres = Flux.fromIterable(lUsuarios);

		Flux<Usuario> flxUsuarios = flxNombres.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
		.filter(usuario -> usuario.getNombre().equalsIgnoreCase("Jonathan"))
		.doOnNext(usuario -> {
			if (usuario == null) {
				throw new RuntimeException("Nombre vacío");
			} 
			System.out.println(usuario.getNombre() + " " + usuario.getApellido());
		})
		.map(usuario -> {
			String nombreLC = usuario.getNombre().toLowerCase();
			usuario.setNombre(nombreLC);
			return usuario;
		});

		flxUsuarios.subscribe(
			elemento -> LOG.info(elemento.toString()), 
			error -> LOG.error(error.getMessage()),
			new Runnable(){
				@Override
				public void run(){
					LOG.info("FLUJO TERMINADO CON ÉXITO!");
				}
			});
	}

}
