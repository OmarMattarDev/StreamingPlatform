public abstract class Person {

    private int id,age;

    private String name;

    public Person(
            int id,
            String name,
            int age
    )
    {
        setId(id);
        setName(name);
        setAge(age);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    private void setName(String name) {
        if(name==null||name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = name;
    }

    private void setId(int id) {
        if(id<=0){
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        this.id = id;
    }

    private void setAge(int age) {
        if(age<=0){
            throw new IllegalArgumentException("Age must be greater than 0");
        }
        this.age = age;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\nID : ").append(id)
                .append("\nName : ").append(name)
                .append("\nAge : ").append(age)
                .append("\n").toString();
    }
}
