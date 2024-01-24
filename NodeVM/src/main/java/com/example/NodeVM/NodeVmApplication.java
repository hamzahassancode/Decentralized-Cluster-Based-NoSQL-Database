package com.example.NodeVM;

//import com.example.NodeVM.repo.IndexingRepo2;
//import com.example.NodeVM.indexing.Indexing;
import com.example.NodeVM.indexing.Indexing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NodeVmApplication {

	public static void main(String[] args) {

		SpringApplication.run(NodeVmApplication.class, args);
		Indexing.setupIndexing();
	}

}
