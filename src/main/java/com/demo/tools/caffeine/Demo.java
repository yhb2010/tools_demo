package com.demo.tools.caffeine;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;

//https://github.com/eugenp/tutorials/tree/master/libraries
public class Demo {

	//手动填充
	public static void test1() {
		Cache<String, DataObject> cache = Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(100).build();
		//现在，我们可以使用 getIfPresent 方法从缓存中获取一些值。 如果缓存中不存在此值，则此方法将返回 null：
		String key = "A";
		DataObject dataObject = cache.getIfPresent(key);
		System.out.println(dataObject);

		//我们可以使用 put 方法手动填充缓存：
		cache.put(key, DataObject.get("d1"));
		dataObject = cache.getIfPresent(key);
		System.out.println(dataObject);

		//我们也可以使用 get 方法获取值，该方法将一个参数为 key 的 Function 作为参数传入。如果缓存中不存在该键，则该函数将用于提供回退值，该值在计算后插入缓存中：
		//get 方法可以原子方式执行计算。这意味着您只进行一次计算 — 即使多个线程同时请求该值。这就是为什么使用 get 优于 getIfPresent。
		key = "B";
		dataObject = cache.get(key, k -> DataObject.get("Data for A"));
		System.out.println(dataObject);

		//有时我们需要手动使一些缓存的值失效：
		cache.invalidate(key);
		dataObject = cache.getIfPresent(key);
		System.out.println(dataObject);
	}

	//同步加载
	//这种加载缓存的方法使用了与用于初始化值的 Function 相似的手动策略的 get 方法。让我们看看如何使用它。
	public static void test2() {
		LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				 .maximumSize(100)
				 .expireAfterWrite(1, TimeUnit.MINUTES)
				 .build(k -> DataObject.get("Data for " + k));
		String key = "C";
		DataObject dataObject = cache.get(key);
		System.out.println(dataObject.getData());

		//我们也可以使用 getAll 方法获取一组值：
		Map<String, DataObject> dataObjectMap = cache.getAll(Arrays.asList("A", "B", "C"));
		System.out.println(dataObjectMap.get("A").getData());
		System.out.println(dataObjectMap.get("B").getData());
		System.out.println(dataObjectMap.get("C").getData());
	}

	//异步加载
	//此策略的作用与之前相同，但是以异步方式执行操作，并返回一个包含值的 CompletableFuture：
	public static void test3() {
		AsyncLoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				 .maximumSize(100)
				 .expireAfterWrite(1, TimeUnit.MINUTES)
				 .buildAsync(k -> DataObject.get("Data for " + k));
		//我们可以以相同的方式使用 get 和 getAll 方法，同时考虑到他们返回的是 CompletableFuture：
		String key = "A";
		cache.get(key).thenAccept(dataObject -> {
			System.out.println(dataObject.getData());
		});
		cache.getAll(Arrays.asList("A", "B", "C")).thenAccept(dataObjectMap -> System.out.println(dataObjectMap.size()));
	}

	//值回收
	//Caffeine 有三个值回收策略：基于大小，基于时间和参考。

	//基于大小回收
	//这种回收方式假定当超过配置的缓存大小限制时会发生回收。 获取大小有两种方法：缓存中计数对象，或获取权重。
	//让我们看看如何计算缓存中的对象。当缓存初始化时，其大小等于零：
	public static void test4(){
		LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				 .maximumSize(1)
				 .build(k -> DataObject.get("Data for " + k));
		System.out.println(cache.estimatedSize());
		//当我们添加一个值时，大小明显增加：
		cache.get("A");
		System.out.println(cache.estimatedSize());
		//我们可以将第二个值添加到缓存中，这导致第一个值被删除：
		cache.get("B");
		//值得一提的是，在获取缓存大小之前，我们调用了 cleanUp 方法。 这是因为缓存回收被异步执行，这种方法有助于等待回收的完成。
		cache.cleanUp();
		System.out.println(cache.estimatedSize());

		//我们还可以传递一个 weigher Function 来获取缓存的大小：
		LoadingCache<String, DataObject> cache2 = Caffeine.newBuilder()
				 .maximumWeight(10)
				 //代表权重，放入一个值权重加5
				 .weigher((k,v) -> 5)
				 .build(k -> DataObject.get("Data for " + k));
		System.out.println(cache2.estimatedSize());
		cache2.get("A");
		System.out.println(cache2.estimatedSize());
		cache2.get("B");
		System.out.println(cache2.estimatedSize());
		//当 weight 超过 10 时，值将从缓存中删除
		cache2.get("C");
		cache2.cleanUp();
		System.out.println(cache2.estimatedSize());
	}

	//基于时间回收
	//这种回收策略是基于条目的到期时间，有三种类型：
	//访问后到期 — 从上次读或写发生后，条目即过期。
	//写入后到期 — 从上次写入发生之后，条目即过期
	//自定义策略 — 到期时间由 Expiry 实现独自计算
	public static void test5(){
		//让我们使用 expireAfterAccess 方法配置访问后过期策略：
		LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				 .expireAfterAccess(5, TimeUnit.MINUTES)
				 .build(k -> DataObject.get("Data for " + k));
		//要配置写入后到期策略，我们使用 expireAfterWrite 方法：
		cache = Caffeine.newBuilder()
				 .expireAfterWrite(10, TimeUnit.SECONDS)
				 .weakKeys()
				 .weakValues()
				 .build(k -> DataObject.get("Data for " + k));
		//要初始化自定义策略，我们需要实现 Expiry 接口：
		cache = Caffeine.newBuilder().expireAfter(new Expiry<String, DataObject>() {
			@Override
			public long expireAfterCreate(String key, DataObject value, long currentTime) {
				return value.getData().length() * 1000;
			}
			@Override
			public long expireAfterUpdate(String key, DataObject value, long currentTime, long currentDuration) {
				return currentDuration;
			}
			@Override
			public long expireAfterRead(String key, DataObject value, long currentTime, long currentDuration) {
				return currentDuration;
			}
		}).build(k -> DataObject.get("Data for " + k));
	}

	//基于引用回收
	//我们可以将缓存配置为启用缓存键值的垃圾回收。为此，我们将 key 和 value 配置为 弱引用，并且我们可以仅配置软引用以进行垃圾回收。
	//当没有任何对对象的强引用时，使用 WeakRefence 可以启用对象的垃圾回收。SoftReference 允许对象根据 JVM 的全局最近最少使用（Least-Recently-Used）的策略进行垃圾回收。
	public static void test6(){
		//我们应该使用 Caffeine.weakKeys()、Caffeine.weakValues() 和 Caffeine.softValues() 来启用每个选项：
		LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				.expireAfterWrite(10, TimeUnit.SECONDS).weakKeys().weakValues()
				.build(k -> DataObject.get("Data for " + k));
		cache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS)
				.softValues().build(k -> DataObject.get("Data for " + k));
	}

	//刷新（Refresh）
	//刷新和驱逐是不一样的。刷新的是通过LoadingCache.refresh(key)方法来指定，并通过调用CacheLoader.reload方法来执行，刷新key会异步地为这个key加载新的value，并返回旧的值（如果有的话）。驱逐会阻塞查询操作直到驱逐作完成才会进行其他操作。
	//与expireAfterWrite不同的是，refreshAfterWrite将在查询数据的时候判断该数据是不是符合查询条件，如果符合条件该缓存就会去执行刷新操作。例如，您可以在同一个缓存中同时指定refreshAfterWrite和expireAfterWrite，只有当数据具备刷新条件的时候才会去刷新数据，不会盲目去执行刷新操作。如果数据在刷新后就一直没有被再次查询，那么该数据也会过期。
	public static void test7() {
		LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				.refreshAfterWrite(1, TimeUnit.SECONDS)
				.expireAfterWrite(1, TimeUnit.SECONDS)
				.build(k -> DataObject.get("Data for " + k));
		System.out.println(cache.get("A"));
		System.out.println(cache.estimatedSize());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(cache.get("A"));
		System.out.println(cache.estimatedSize());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(cache.getIfPresent("A"));
		cache.cleanUp();
		System.out.println(cache.estimatedSize());
	}

	//统计
	//Caffeine 有一种记录缓存使用情况的统计方式：
	//我们也可能会传入 recordStats supplier，创建一个 StatsCounter 的实现。每次与统计相关的更改将推送此对象。
	public static void test8(){
		LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
				.maximumSize(100).recordStats()
				.build(k -> DataObject.get("Data for " + k));
		cache.get("A");
		cache.get("A");

		System.out.println(cache.stats().hitCount());
		System.out.println(cache.stats().missCount());
	}


	public static void main(String[] args) {
		test1();
		System.out.println("--------------------------");
		test2();
		System.out.println("--------------------------");
		test3();
		System.out.println("--------------------------");
		test4();
		System.out.println("--------------------------");
		test7();
	}

}
