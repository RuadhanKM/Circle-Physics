import java.util.List;
import java.util.ArrayList;

class Vector2
{
	Double x;
	Double y;
	
	public Vector2(Double x, Double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Double Mag(){
		return Math.abs(Math.sqrt((this.x*this.x)+(this.y*this.y)));}
	
	public Vector2 Norm() {
        Double s = 1.0 / this.Mag();
        return new Vector2(this.x*s, this.y*s);}
	
	public Vector2 Inv(){
		return new Vector2(this.x*-1, this.y*-1);}
	
	public Double Dot(Vector2 b){
		return this.x * b.x + this.y * b.y;}
	
	public Vector2 Mult(Double b){return new Vector2(this.x * b, this.y * b);}public Vector2 Mult(Vector2 b){return new Vector2(this.x * b.x, this.y * b.y);}
	public Vector2 Div(Double b){return new Vector2(this.x / b, this.y / b);}public Vector2 Div(Vector2 b){return new Vector2(this.x / b.x, this.y / b.y);}
	public Vector2 Sub(Double b){return new Vector2(this.x - b, this.y - b);}public Vector2 Sub(Vector2 b){return new Vector2(this.x - b.x, this.y - b.y);}
	public Vector2 Add(Double b){return new Vector2(this.x + b, this.y + b);}public Vector2 Add(Vector2 b){return new Vector2(this.x + b.x, this.y + b.y);}
}


class Particle
{
	Vector2 Pos;
	Double Rad;
	Vector2 Vel;
	String C;
	Double Elas;
	
	public Particle(Vector2 Pos, Vector2 Vel, Double Rad, String C, Double Elas)
	{
		this.Pos = Pos;
		this.Rad = Rad;
		this.Vel = Vel;
		this.C = C;
		this.Elas = Elas;
	}
	
	public void doGravity(Double g, Double dt)
	{
		this.Vel = new Vector2(this.Vel.x, this.Vel.y - (g*dt));
	}
	
	public void doMove(Double dt)
	{
		this.Pos = this.Pos.Add(this.Vel.Mult(dt));
		if (this.Pos.Mag()+this.Rad > 20)
		{
			Vector2 n = this.Pos.Norm().Inv();
			while (this.Pos.Mag()+this.Rad > 20)
			{
				Double diff = (this.Pos.Mag()+this.Rad - 20) + 0.02;
				this.Pos = this.Pos.Add(n.Mult(diff));
			}

			this.Vel = this.Vel.Sub(n.Mult(this.Vel.Dot(n) * 2)).Mult(Elas);
		}
	}
}

class Engine
{
	Vector2 res;
	
	public Engine(Vector2 res)
	{
		this.res = res;
	}
	
	public void Render(Particle[] plist, String info)
	{
		String fin = "";
		for (Double y = -this.res.y/2; y <= this.res.y/2; y += 1)
		{
			String line = "";
			for (Double x = -this.res.x/2; x <= this.res.x/2; x += 1)
			{
				Vector2 pixel = new Vector2(x, -y);
				String Char = "  ";
				for (Particle c : plist)
				{
					if (new Vector2(c.Pos.x-pixel.x, c.Pos.y-pixel.y).Mag() <= c.Rad || pixel.Mag() > 20)
					{
						Char = c.C;
						break;
					}
				}
				line += Char;
			}
			fin += line + "\n";
		}
		System.out.println(fin + "\n" + info);
	}
}

public class Main
{
	public static void main(String[] args)
	{
		Engine e = new Engine(new Vector2(50.0, 50.0));
		List<Particle> plal = new ArrayList<Particle>();
		
		for (Double i=0.0; i<Double.parseDouble(args[0]); i+=1.0)
		{
			Double t = Double.parseDouble(args[0]);
			Double m = t-i;
			Double g = (2.0*3.1415926535)/(i+m);
			
			plal.add(new Particle(new Vector2(0.0, 0.0), new Vector2(Math.sin(g*i)*Double.parseDouble(args[2])+Double.parseDouble(args[3]), Math.cos(g*i)*Double.parseDouble(args[2])), 1.5, "# ", Double.parseDouble(args[4])));
		}
		
		Particle[] pl = new Particle[plal.size()];
		plal.toArray(pl);
		
		Double dt = 0.0;
		Long lt = System.nanoTime();
		
		while (true)
		{
			for (Particle pp : pl)
			{
				pp.doGravity(100.0, dt);
				pp.doMove(dt);
			}
			
			e.Render(pl, Double.toString(dt));
			
			dt = ((double)(System.nanoTime() - lt)/1e9)/Double.parseDouble(args[1]);
			lt = System.nanoTime();
		}
	}
}