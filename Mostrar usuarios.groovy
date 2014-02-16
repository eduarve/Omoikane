import omoikane.sistema.Usuarios;
Usuarios sysUsers = omoikane.principal.Principal.applicationContext.getBean(Usuarios.class);
sysUsers.getUsuarioRepo().findAll().each {

	println it.nombre;
}

sysUsers.getUsuarioRepo().readByPrimaryKey(1L).nombre;
