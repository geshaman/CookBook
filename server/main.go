package main

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
	"strconv"
	"strings"
)

type Step struct {
	Text         string `json:"text"`
	TimerSeconds *int   `json:"timerSeconds"`
}

type Recipe struct {
	ID             int      `json:"id"`
	Title          string   `json:"title"`
	Description    string   `json:"description"`
	ImageURL       string   `json:"imageUrl"`
	CookingMinutes int      `json:"cookingMinutes"`
	Ingredients    []string `json:"ingredients"`
	Steps          []Step   `json:"steps"`
	ImageFile      string   `json:"-"` // в JSON не попадает
}

func ptr(n int) *int { return &n }

var recipes = []Recipe{
	{
		ID:             1,
		Title:          "Блины с творогом",
		Description:    "Тонкие блинчики с нежной творожной начинкой.",
		ImageFile:      "pancakes.jpg",
		CookingMinutes: 40,
		Ingredients:    []string{"Мука, 200 г", "Молоко, 500 мл", "Яйца, 2 шт.", "Творог, 300 г"},
		Steps: []Step{
			{Text: "Смешайте муку, молоко и яйца до однородного теста."},
			{Text: "Обжарьте тонкие блинчики с двух сторон.", TimerSeconds: ptr(120)},
			{Text: "Заверните творог в блинчики."},
		},
	},
	{
		ID:             2,
		Title:          "Паста Болоньезе",
		Description:    "Вкуснейшая паста с густым, насыщенным мясным соусом рагу",
		ImageFile:      "bolon.jpg",
		CookingMinutes: 40,
		Ingredients: []string{"Лук репчатый, 1 шт.", "Чеснок, 2 зубчика", "Оливковое масло, 2 ст. л.",
			"Мясной фарш, 600 г.", "Сладкий перец, 2 шт.", "Помидоры 500 г.", "Спаггети, 250 г.", "Тёртый сыр 100 г."},
		Steps: []Step{
			{Text: "Приготовьте соус для спагетти болоньезе. Для этого лук с чесноком очистите. Лук разрежьте на половинки, затем нарежьте мелкими кубиками. Зубчики чеснока раздавите плоской стороной ножа и порубите."},
			{Text: "В большую сковороду налейте оливковое масло и нагрейте его. Выложите нарезанные лук с чесноком и, помешивая, жарьте на среднем огне до легкого подрумянивания."},
			{Text: "К луку с чесноком добавьте фарш и, помешивая вилкой (чтобы не образовывались комочки), жарьте на сковороде на среднем огне в течение 10 минут."},
			{Text: "Сладкий перец, удалив семена и перегородки, нарежьте маленькими кубиками. Добавьте в сковороду к мясу и, помешивая, жарьте 5 минут. Следите, чтобы перец не подгорел."},
			{Text: "Помидоры очистите и переложите в чашу блендера. Измельчите на высокой скорости, затем добавьте в сковороду к мясу с овощами. Посолите и поперчите. Добавьте пряные травы по вкусу и тушите соус болоньезе на маленьком огне 20 минут."},
			{Text: "Приготовьте спагетти. Для этого в большой кастрюле доведите до кипения воду и немного посолите ее. Опустите в нее спагетти, не разламывая. Делайте это постепенно, по мере размягчения погруженной в воду части. Варите 10 минут.  "},
			{Text: "Готовые спагетти откиньте на дуршлаг и дайте стечь жидкости. Промывать их не надо: так паста будет лучше соединяться с соусом. Но добавить столовую ложку оливкового масла можно."},
			{Text: "Спагетти положите в соус болоньезе и перемешайте. Прогрейте на сковороде все вместе. Затем разложите по порционным тарелкам и посыпьте тертым сыром. Сразу подайте на стол."},
		},
	},
	{
		ID:             3,
		Title:          "Омлет с сыром",
		Description:    "Пышный омлет с расплавленным сыром, готовится за 10 минут.",
		ImageFile:      "omelet.jpg",
		CookingMinutes: 10,
		Ingredients:    []string{"Яйца, 3 шт.", "Молоко, 50 мл", "Сыр, 50 г", "Сливочное масло, 10 г"},
		Steps: []Step{
			{Text: "Взбейте яйца с молоком и щепоткой соли."},
			{Text: "Растопите масло на сковороде и влейте яичную смесь."},
			{Text: "Готовьте под крышкой на небольшом огне.", TimerSeconds: ptr(180)},
			{Text: "Посыпьте тёртым сыром и сложите омлет пополам."},
		},
	},
}

// Ссылку на картинку строим по адресу, с которого пришёл запрос:
// с эмулятора это 10.0.2.2, с телефона IP компьютера.
func withImageURL(r *http.Request, rec Recipe) Recipe {
	scheme := "http"
	if r.Header.Get("X-Forwarded-Proto") == "https" {
		scheme = "https"
	}
	rec.ImageURL = scheme + "://" + r.Host + "/images/" + rec.ImageFile
	if rec.Ingredients == nil {
		rec.Ingredients = []string{}
	}
	if rec.Steps == nil {
		rec.Steps = []Step{}
	}
	return rec
}

func writeJSON(w http.ResponseWriter, v any) {
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	json.NewEncoder(w).Encode(v)
}

func main() {
	http.HandleFunc("/recipes", func(w http.ResponseWriter, r *http.Request) {
		result := make([]Recipe, 0, len(recipes))
		for _, rec := range recipes {
			result = append(result, withImageURL(r, rec))
		}
		writeJSON(w, result)
	})

	http.HandleFunc("/recipes/", func(w http.ResponseWriter, r *http.Request) {
		id, err := strconv.Atoi(strings.TrimPrefix(r.URL.Path, "/recipes/"))
		if err != nil {
			http.NotFound(w, r)
			return
		}
		for _, rec := range recipes {
			if rec.ID == id {
				writeJSON(w, withImageURL(r, rec))
				return
			}
		}
		http.NotFound(w, r)
	})

	http.Handle("/images/", http.StripPrefix("/images/", http.FileServer(http.Dir("images"))))

	log.Println("server started on :8080")
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
